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

## 実装手順

Azure上へ実装手順はQiitaに書きました。

https://qiita.com/shingo_kawahara/items/cc5f41c1cdab5f89747d
