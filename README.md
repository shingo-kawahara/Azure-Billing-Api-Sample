# Azure-Billing-Api-Sample

Azure Functionsにデプロイして利用します。

利用する側は、以下の情報を元にGETリクエストを発行することで、前月末～実行日までのAzureサブスクリプション利用料を確認することができます。

* functionsKey
* subscriptoinId
* clientId
* clientSecret
* tenantId

## PowerShell実行例

```
Invoke-RestMethod -Uri "https://xxxxx.azurewebsites.net/api/billing" -Method GET -Body @{code="$functionsKey";subscriptionId="$subscriptionId";clientId="$clientId";clientSecret="$clientSecret";tenantId="$tenantId"}
```
