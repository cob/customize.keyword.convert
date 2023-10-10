#COB _convertTO

### What is this script for ?   
    *This script is used for instances with fields with the description '$file $convert(neighborFieldName).pdf'.
    *It gets the content from the field *neighborFieldName*, and converts it to PDF using the
    *https://www.convertapi.com/ endpoints.

    * The field *neighborFieldName* must have one of the three following descriptions:
        * $text
        * $markdown
        * $file. The supported file extensions can be found at /integrationm/common/config/ConvertToConfig.SUPPORTED_INPUTS

    * It currently converts plain text, markdown and files to PDF, with the resulting PDF having a
    useful width of 770.
    
### Needed credentials
    * In order to successfully use the https://www.convertapi.com/ endpoints, the user
    * must include the API KEY in the file common/config/ConvertConfig.groovy