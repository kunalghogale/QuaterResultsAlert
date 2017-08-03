<html>
<head>
    <title>${title}</title>
</head>
<body>
<h1 align="center">${title}</h1>

<table align="center" border="4">
    <thead>
    <tr>
        <th>Time of Day</th>
        <th>Company Name</th>
        <th>Market Capital</th>
        <th>Expected EPS</th>
    </tr>
    </thead>
<#list stocks as stock>
    <tr>
        <td>${stock.timeOfDay}</td>
        <td>
            <#assign url = "http://www.nasdaq.com/symbol/" + stock.symbol + "/real-time">
            <a href=${url}>${stock.companyName}</a></td>
        <td>${stock.marketCap}</td>
        <#if stock.epsForecast?contains("-")>
            <td bgcolor="red">${stock.epsForecast}</td>
        <#elseif stock.epsForecast?contains("n/a")>
            <td>${stock.epsForecast}</td>
        <#else>
            <td bgcolor="green">${stock.epsForecast}</td>
        </#if>
    </tr>
</#list>
</table>

</body>
</html>