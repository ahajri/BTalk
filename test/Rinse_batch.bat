md src1

"D:\DEV\java\jdk1.8.0_77\bin\xjc.exe" "http://www.xso.nl/jaxb/XmlAuditfileFinancieel3.2.xsd" -verbose -d ".\src1" -p xso.bd.auditfile
rem ------------- [end src1]
pause


md src2
"D:\DEV\java\jdk1.8.0_77\bin\xjc.exe" "http://www.nltaxonomie.nl/10.0/report/bd/entrypoints/bd-rpt-ob-aangifte-2016.xsd" -verbose -d ".\src2" -p xso.bd.aangifte2016

rem ------------------------- [ end ] 

pause
rem -------------------------------[begin]
md src1

"D:\DEV\java\jdk1.8.0_77\bin\xjc.exe" "http://www.xso.nl/jaxb/XmlAuditfileFinancieel3.2.xsd" -verbose -d ".\src1" -p xso.bd.auditfile
pause


md src2
"D:\DEV\java\jdk1.8.0_77\bin\xjc.exe" "http://www.nltaxonomie.nl/10.0/report/bd/entrypoints/bd-rpt-ob-aangifte-2016.xsd" -verbose -d ".\src2" -p xso.bd.aangifte2016
pause
rem ----------------------------------[end]