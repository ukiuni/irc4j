{
	"pluginInfoTitle":"Plugin",
	"pluginInfoSubtitle":"AIRCはPluginで機能を拡張することができます。",
	"pluginInfoAboutPluginTitle":"Pluginの作成方法",
	"pluginInfoAboutPlugin":"タイトルバーのメニューのPluginを押してください。<br/>create new ボタンを押すと、新しくPluginを作成することができます。<br/>commandで指定したコマンドをIRCクライアントから実行すると、scriptで定義したexecuteファンクションが実行されます。",
	"pluginInfoAboutPluginFunctionTitle":"Pluginで利用可能なfunction",
	"pluginInfoAboutMethodExecute":"execute(server, client, params)",
	"pluginInfoAboutMethodExecuteMessage":"このメソッドは、commandで指定されたコマンドがIRCクライアントから送信された時に実行されます。serverはgetChannel等でサーバの情報を取得できます。clientは送信元の情報が取得できます。paramsはIRCクライアントから送信された情報をスペースで分割した配列です。",
	"pluginInfoAboutMethodExecuteMessageSmall":"サーバーメッセージを返信します。",
	"pluginInfoAboutMethodExecuteMessageSmall2":"指定したチャネルにメッセージを送信します。<br>\"/COMMAND channelName message\" というフォーマットを期待した実装です。",
	"pluginInfoAboutMethodSetTimeout":"setTimeout(function, delayTime)",
	"pluginInfoAboutMethodSetTimeoutMessage":"delayTimeミリ秒後にfunctionを実行します。",
	"pluginInfoAboutMethodSetTimeoutMessageSmall":"10秒後にサーバーメッセージを返信します。",
	"pluginPaneLinkToInfo":"Pluginの説明"
}