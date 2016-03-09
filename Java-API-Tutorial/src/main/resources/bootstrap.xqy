xquery version "1.0-ml";

module namespace bootstrap = "http://marklogic.com/rest-api/resource/bootstrap";
(: Copyright 2002-2010 Mark Logic Corporation.  All Rights Reserved. :)

import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";

declare default function namespace "http://www.w3.org/2005/xpath-functions";

declare variable $p := "http://marklogic.com/ns/test/places";

declare function bootstrap:database-configure(
  $dbid as xs:unsignedLong)
as empty-sequence()
{

    let $c := admin:get-configuration()
    let $rangespec := admin:database-range-element-index("date",
                                (),"week","",false() )
    let $c := admin:database-add-range-element-index($c, $dbid, $rangespec)
    let $rangespec := admin:database-range-element-index("string",
        "","genre","http://marklogic.com/collation/",false() )
    let $c := admin:database-add-range-element-index($c, $dbid, $rangespec)
    return admin:save-configuration-without-restart($c)
};

declare function bootstrap:security-config(
$command as xs:string
) 
{ 
    try {
        xdmp:eval(fn:concat('xquery version "1.0-ml"; ',
                    'import module namespace sec="http://marklogic.com/xdmp/security" at  ',
                    '    "/MarkLogic/security.xqy"; ',
                    $command),
        (),
        <options xmlns="xdmp:eval">
            <database>{ xdmp:database("Security") }</database>
        </options>)
    } catch($e) {
        xdmp:log($e)
    }
};



declare function bootstrap:post(
    $context as map:map,
    $params  as map:map,
    $input as document-node()*
) as document-node()*
{
    let $ncre := bootstrap:security-config('sec:create-user("rest-admin","rest-admin user", "x",("rest-admin"),(),())')
    let $ncre := bootstrap:security-config('sec:create-user("rest-reader","rest-reader user", "x",("rest-reader"),(),())')
    let $ncre := bootstrap:security-config('sec:create-user("rest-writer","rest-writer user", "x",("rest-writer"),(),())')
    let $dbid := xdmp:database("TopSongs")
    let $config := bootstrap:database-configure($dbid)
    return ()
};

