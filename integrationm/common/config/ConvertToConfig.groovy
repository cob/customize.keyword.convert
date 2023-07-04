package config

class ConvertToConfig {
    public static final String API_KEY = "xxxxxx";
    public static final String MD_CSS_STYLE = ("body{font-family: Verdana, arial, sans-serif;} code{\n" +
            "        padding: 2px 4px;\n" +
            "        color: #d14;\n" +
            "        background-color: #f7f7f9;\n" +
            "        border: 1px solid #e1e1e8;\n" +
            "    }\n" +
            "    blockquote {\n" +
            "        display: block;\n" +
            "        margin-block-start: 1em;\n" +
            "        margin-block-end: 1em;\n" +
            "        margin-inline-start: 40px;\n" +
            "        margin-inline-end: 40px;\n" +
            "\n" +
            "        padding: 0 0 0 15px;\n" +
            "        margin: 0 0 20px;\n" +
            "        border-left: 5px solid #eee;\n" +
            "        color: black;\n" +
            "    }\n" +
            "\n" +
            "    blockquote:before, blockquote:after {\n" +
            "        content: none;\n" +
            "    }\n" +
            "    blockquote p {\n" +
            "        margin-bottom: 0;\n" +
            "        font-size: 15px;\n" +
            "        font-weight: 300;\n" +
            "        line-height: 1.25;\n" +
            "        color: black;\n" +
            "        font-weight: 575;\n" +
            "    }\n" +
            "    pre {\n" +
            "        background: #eee;\n" +
            "        margin-bottom: 10px;\n" +
            "    }\n" +
            "\n" +
            "    pre {\n" +
            "        padding: 16px;\n" +
            "        overflow: auto;\n" +
            "        line-height: 1.45;\n" +
            "        background-color: #f6f8fa;\n" +
            "        border-radius: 3px;\n" +
            "        word-wrap: normal;\n" +
            "        margin-top: 0;\n" +
            "        margin-bottom: 16px;\n" +
            "    }\n" +
            "    pre code {\n" +
            "        padding: 0;\n" +
            "        margin: 0;\n" +
            "        font-size: 100%!;\n" +
            "        color: #24292e;\n" +
            "        border:0;\n" +
            "        word-break: normal;\n" +
            "        white-space: pre;\n" +
            "        background: transparent;\n" +
            "    }\n" +
            "    table, table th, table td {\n" +
            "        border: 1px solid #ddd;\n" +
            "    }\n" +
            "    table {\n" +
            "        max-width: 100%;\n" +
            "        margin-bottom: 20px;\n" +
            "        border-collapse: collapse;\n" +
            "        border-spacing: 0;\n" +
            "    }\n" +
            "    table th {\n" +
            "        border-top: 0;\n" +
            "    }\n" +
            "    table>thead>tr>th, table>tbody>tr>td {\n" +
            "        border-bottom-width: 2px;\n" +
            "        padding: 8px;\n" +
            "        line-height: 1.4285714;\n" +
            "        vertical-align: top;\n" +
            "    }\n" +
            "    a{\n" +
            "        color: #3399CC;\n" +
            "        text-decoration: none;\n" +
            "    }\n" +
            "    a:hover, a:focus {\n" +
            "        color: #246b8f;\n" +
            "        text-decoration: underline;\n" +
            "        outline: 0;\n" +
            "    }\n" +
            "    h3 {\n" +
            "    font-size: 21px;\n" +
            "    }").replaceAll("\n","");
}