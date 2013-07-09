<?xml version="1.0" encoding="UTF-8"?>
<dataStore>
  <name>${datastoreName}</name>
  <type>PostGIS</type>
  <enabled>true</enabled>
  <connectionParameters>
    <entry key="host">${databaseHost}</entry>
    <entry key="port">${databasePort?c}</entry>
    <entry key="dbtype">postgis</entry>
    <entry key="schema">public</entry>
    <entry key="database">${databaseName}</entry>
    <entry key="user">${databaseUsername}</entry>
    <entry key="passwd">${databasePassword}</entry>
    <entry key="namespace">${namespaceUri}</entry>
    <entry key="Connection timeout">20</entry>
    <entry key="validate connections">false</entry>
    <entry key="min connections">1</entry>
    <entry key="max connections">10</entry>
    <entry key="Loose bbox">true</entry>
    <entry key="fetch size">1000</entry>
    <entry key="Max open prepared statements">50</entry>
    <entry key="preparedStatements">false</entry>
  </connectionParameters>
</dataStore>
