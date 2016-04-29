rem md src1

rem "D:\DEV\java\jdk1.8.0_77\bin\xjc.exe" "http://www.xso.nl/jaxb/XmlAuditfileFinancieel3.2.xsd" -verbose -d ".\src1" -p xso.bd.auditfile

rem pause


md src2
"D:\DEV\java\jdk1.8.0_77\bin\xjc.exe" "http://www.nltaxonomie.nl/10.0/report/bd/entrypoints/bd-rpt-ob-aangifte-2016.xsd" -verbose -d ".\src2" -p xso.bd.aangifte2016


pause
