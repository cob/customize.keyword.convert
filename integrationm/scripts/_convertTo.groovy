import groovy.transform.Field
import org.codehaus.jettison.json.JSONObject

import com.google.common.cache.*
import java.util.concurrent.TimeUnit

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Base64;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity
import javax.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.media.multipart.*;
import org.glassfish.jersey.media.multipart.file.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;

//log.info("DAN. AUTOPDF.GROOVY ONNN ddsfds !!! '$msg.type'")
log.info("STARTING _convertToPDF.groovy")

if ( msg.type != "testAuto" ) {
    return
}
if (msg.product != "recordm-definition" && msg.product != "recordm") {
    return
}

@Field static cacheOfAuditFieldsForDefinition = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();

if (msg.product == "recordm-definition") cacheOfAuditFieldsForDefinition.invalidate(msg.type)

// ========================================================================================================
def auditFields = cacheOfAuditFieldsForDefinition.get(msg.type, { getAuditFields(msg.type) })
if (auditFields.size() > 0 && msg.product == "recordm" && msg.action =~ "add|update") {
    if (msg.user != "integrationm" || auditFields.every { f -> !msg.field(f.name).changed() }) {
        recordm.update(msg.type, msg.instance.id, getAuditFieldsUpdates(auditFields, msg.instance.fields));
    }
}

// ========================================================================================================
def getAuditFieldsUpdates(auditFields,instanceFields) {
    def updates = [:]
    log.info("IN getAuditFieldsUpdates. Fields: ${auditFields.size()}")
    auditFields.each { auditField ->

        def currentValue = msg.value(auditField.sourceField)
        if( ( msg.action == 'add' || (msg.action == 'update' && msg.field(auditField.sourceField).changed()) )
        && currentValue != null ) {
            def matcher = currentValue =~ /[A-Za-z0-9]+\.(?i)(xls|xlsx)/

            if (matcher.size() > 0) {
                try {
                    String filename = currentValue.split("\\.")[0];
                    def absPath = "/var/lib/recordm/attachments/${msg.getInstance().field(auditField.sourceField).getFilePath()}/${currentValue}"
                    def destPath = "/tmp/${filename}.pdf"
                    File f = new File(absPath);
                    long len = f.length();
                    if(f.exists()){
                        if(convertFile(f,filename,destPath,msg.instance.id,auditField.name)){
                            currentValue = filename+".pdf"
                            log.info("value '$currentValue'")
                            updates << [(auditField.name) : currentValue]
                        }else{
                            log.error("CONVERSION DID NOT HAPPEN")
                        }
                    }else{
                        log.error("NOT FOUND FILE '${absPath}'")
                    }
                }catch(Exception e){
                    e.printStackTrace()
                    log.error("${e.getMessage()}");
                }
            }else{
                log.error("EXPECTED EXTENSION 'XLS' OR 'XLSX'. GOT '${currentValue}'")
            }   
        }
    }
    log.info("[\$convert.pdf] Update 'convertPDFS' for updates: '$updates'");
    return updates
}

def getAuditFields(definitionName) {
    log.info("IN getAuditFields")
    // Obtém detalhes da definição
    def definitionEncoded = URLEncoder.encode(definitionName, "utf-8").replace("+", "%20")
    def resp = actionPacks.rmRest.get( "recordm/definitions/name/${definitionEncoded}".toString(), [:], "integrationm");
    JSONObject definition = new JSONObject(resp);

    def fieldsSize = definition.fieldDefinitions.length();

    def fields = [:]
    (0..fieldsSize-1).each { index ->
        def fieldDefinition  = definition.fieldDefinitions.getJSONObject(index)
        def fieldDescription = fieldDefinition.getString("description")
        if(fieldDescription){
            def fieldDefId       = fieldDefinition.get("id");
            def fieldName        = fieldDefinition.get("name");
            fields[fieldDefId]   = [name:fieldName, description: fieldDescription]
        }
    }

    // Finalmente obtém a lista de campos que é necessário calcular
    def auditFields = [];
    fields.each { fieldId,field -> 
        log.info("desc: $field.description")
        def matcher = field.description =~ /[$]convert\(([^)].*)\)\.pdf/
        if( matcher.size() > 0) {
            def sourceField = matcher[0][1]
            log.info("sourceField '$sourceField'")
            auditFields << [fieldId: fieldId, name:field.name, sourceField:sourceField ]
        }
    }
    log.info("[\$convert.pdf] Update 'auditFields' for '$definitionName': $auditFields");
    return auditFields
}

def convertFile(File file,String filename, String destPath,int instanceId, String fieldName){

    def String TARGET_URL="https://v2.convertapi.com/convert/xls/to/pdf?Secret=dzI3yJPWjuiYO8YA";

    Client client = ClientBuilder.newClient()
        .property(ClientProperties.CONNECT_TIMEOUT, 3000)
        .property(ClientProperties.READ_TIMEOUT, 90000)
        .register(JacksonFeature.class)
        .register(MultiPartFeature.class);
    WebTarget webTarget = client.target(TARGET_URL);

    MultiPart multiPart = new MultiPart();
    multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

    FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("File",file,MediaType.valueOf("application/pdf"));
    multiPart.bodyPart(fileDataBodyPart);

    multiPart.bodyPart(new FormDataBodyPart( "StoreFile", "true"));
    multiPart.bodyPart(new FormDataBodyPart( "filename",filename));
    multiPart.bodyPart(new FormDataBodyPart( "WorksheetIndex","1"));

    Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(multiPart, multiPart.getMediaType()));

    String jsonResponse = response.readEntity(String.class);
    JSONObject jsonObject = new JSONObject(jsonResponse);
    JSONArray jsonArray = jsonObject.getJSONArray("Files");
    String url = jsonArray.getJSONObject(0).getString("Url");

    log.info ("Finished convertion. STATUS: ${response.getStatus()} URL ${url}")
    if(200 == response.getStatus()){
        File fileToDownloaded;
        try{
            fileToDownloaded = new File(destPath);
            FileUtils.copyURLToFile(new URL(url),fileToDownloaded);
            int status = client.target(url).request().delete().getStatus();
            //log.info("DELETED ? ${status}")
            if(!recordm.attach(instanceId,fieldName,fileToDownloaded.getName(),fileToDownloaded).success()){
                log.error("DOWNLOADED CONVERTED PDF FILE NOT ATTACHED")
            }
            return true
        }catch(Exception e){
            log.error("${e.getMessage()}")
            e.printStackTrace()
        }finally{
            fileToDownloaded.delete()
        }
    }else{
        log.error("ERROR CONVERTING THE FILE TO PDF. RESPONSE STATUS: ${response.getStatus()}")
    }
    return false;
}