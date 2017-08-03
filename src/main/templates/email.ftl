<html>
<head>
    <title>${title}</title>
</head>
<body>
<h1>${title}</h1>

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
        <td>${stock.companyName}</td>
        <td>${stock.marketCap}</td>
        <td>${stock.epsForecast}</td>
    </tr>
</#list>
</table>

</body>
</html>