//const EVENT_GENRE_STARTREFRESH=1;
//const EVENT_GENRE_FINISHREFRESH=2;
//const EVENT_NEW=3;
//const EVENT_CURRENT=4;
//const NOTIFICATION_NEW=1;
//const NOTIFICATION_CURRENT=2;
//const NOTIFICATION_CANCEL=3;
//const NOTIFICATION_FINISH=4;

var counter = 0;
var Interactor = function() {
	function isJSC() {
		// You can determine which JS engine is in use at runtime through
		// the following javascript snippet:
		// (from http://code.google.com/p/android/issues/detail?id=12987#c117)
		return window.devicePixelRatio
				&& escape(navigator.javaEnabled.toString()) !== 'function%20javaEnabled%28%29%20%7B%20%5Bnative%20code%5D%20%7D';
	}

	function isGingerbread() {
		var androidAPI = g_api_androidAPI;
		return 9 <= androidAPI && androidAPI <= 10;
	}

	function shouldUseGingerbreadWorkaround() {
		// The addJavaScriptInterface is encountered on gingerbread with
		// JSC javascript engine
		// So use workaround
		return isGingerbread() && isJSC();
	}

	function ApiSystemError(msg) {
		this.name = 'AndroidApiError';
		this.message = msg;
	}

	var api = shouldUseGingerbreadWorkaround() ? null : window.Android, stringify = JSON.stringify, // You
	// should
	// also
	// include
	// json2.js
	parse = JSON.parse; // from https://github.com/douglascrockford/JSON-js

	function callAndroid(name, args) {
		if (!args)
			args = [];

		// var ret = stringify({
		// method : name,
		// params : args
		// });
		var ret = prompt(g_api_signature + stringify({
			method : name,
			params : args
		}));
		if (ret && typeof ret == 'string') {
			if (ret.charAt(0) == '{') {
				ret = parse(ret);
				return ret.result;
			} else {
				// Error, a JSON object should have returned
				api_done(ret);
				throw new ApiSystemError(ret);
			}
		} else {
			api_done("Api returned nothing!");
			throw new ApiSystemError('Api returned nothing!');
		}
	}

	/***************************************************************************
	 * the api_xxx functions are workaround for gingerbread bug
	 * http://code.google.com/p/android/issues/detail?id=12987
	 */
	function api_Print(str) {
		// Please note that it is not even necessary for javascript
		// interface to be really added. In such case we just call
		// Java through prompt
		if (api)
			return api[method](str);
		return callAndroid("Print", [ str ]);
	}
	function api_loaded() {
		if (api)
			return api[method]();
		return callAndroid("loaded");
	}
	function api_done(str) {
		if (api)
			return api[method](str);
		return callAndroid("done", [ str ]);
	}
	function api_AddItem(parentId, url) {
		if (api)
			return api[method](parentId, url);
		return callAndroid("AddItem", [ parentId, url ]);
	}
	function api_FinishItem(id) {
		if (api)
			return api[method](id);
		return callAndroid("FinishItem", [ id ]);
	}
	function api_SaveItem(id, albumId, link, title, author,
			description, date_creation) {
		if (api)
			return api[method](id, albumId, link, title,
					author, description, date_creation);
		return callAndroid("SaveItem", [ id, albumId, link,
				title, author, description, date_creation ]);
	}
	function api_GetLink(id) {
		if (api)
			return api[method](id);
		return callAndroid("GetLink", [ id ]);
	}
	function api_GetParentId(id) {
		if (api)
			return api[method](id);
		return callAndroid("GetParentId", [ id ]);
	}
	function api_FindIdByLink(link) {
		if (api)
			return api[method](link);
		return callAndroid("FindIdByLink", [ link ]);
	}
	function api_Notification(event, genreId, cnt) {
		if (api)
			return api[method](event, genreId, cnt);
		return callAndroid("Notification", [ event, genreId, cnt ]);
	}
	function api_RequestGet(str) {
		return api_RequestGetCharset(str, "UTF-8");
	}
	function api_RequestGetCharset(str, charset) {
		if (api)
			return api[method](event, genreId, cnt);
		return callAndroid("RequestGetCharset", [ str, charset ]);
//		var xmlhttp = new XMLHttpRequest();
//		try {
//			xmlhttp.open('GET', str, false);
//			xmlhttp.setRequestHeader('Content-Type', 'text/html; charset="'
//					+ charset + '"');
//			xmlhttp.send();
//			console.log("RESULT "+str+" " + xmlhttp.status);
//			if (xmlhttp.status == 200) {
//				return xmlhttp.responseText;
//			} else {
//				return "Error: " + xmlhttp.statusText;
//			}
//		} catch (e) {
//			console.log("Exception " + e.name + " with URL=" + str);
//			api_done();
//		}
	}
	function api_Start(id) {
		if (api)
			return api[method](id);
		return callAndroid("Start", [ id ]);
	}
	function api_Finish() {
		if (api)
			return api[method]();
		return callAndroid("Finish");
	}

	return {
		Print : function(str) {
			return api_Print("" + str);
		},
		loaded : function() {
			return api_loaded();
		},
		done : function(str) {
			return api_done("" + str);
		},
		AddItem : function(parentId, url) {
			return api_AddItem(parentId, url);
		},
		FinishItem : function(id) {
			return api_FinishItem(id);
		},
		SaveItem : function(id, albumId, link, title,
				author, description, date_creation) {
			return api_SaveItem(id, albumId, link, title,
					author, description, date_creation);
		},
		GetLink : function(id) {
			return api_GetLink(id);
		},
		GetParentId : function(id) {
			return api_GetParentId(id);
		},
		FindIdByLink : function(link) {
			return api_FindIdByLink(link);
		},
		Notification : function(event, genreId, cnt) {
			return api_Notification(event, genreId, cnt);
		},
		RequestGet : function(str) {
			return api_RequestGet(str);
		},
		RequestGetCharset : function(str, charset) {
			return api_RequestGetCharset(str, charset);
		},
		Start : function(id) {
			return api_Start(id);
		},
		Finish : function() {
			return api_Finish();
		}
	}

}();

// function api_getItems(parentId){ //get stories by cursor
// // if(!Interactor.IsUpdate())return;
// var amount = Interactor.GetLinks("Picture",parentId);
//	
// if(amount>0){
// // Interactor.Notification(NOTIFICATION_NEW,0,0);
// Interactor.Print("Found "+amount+" new pictures in plugin "+parentId+".");
// }else{
// // Interactor.Notification(NOTIFICATION_CANCEL,0,0);
// Interactor.Print("In plugin "+parentId+" not found new pictures.");
// return;
// }
//	
// for(i=0;i<amount;i++){
// counter++;
// if(Interactor.IsCancel())break;
//
// url=Interactor.GetLink();
// GetItem("Picture",url);
// // FeederAPI.Notification(NOTIFICATION_CURRENT,0,1);
//
// }
// // Interactor.ResetLinks();
// };

function Start(id) {
	if (id == 1) {// ���������� ���������� ��������
		counter = 0;
		// Interactor.Trace("Start updating plugins.");
		// FeederAPI.Notification(NOTIFICATION_FINISH,0,counter*1);
	} else if (id == 2) {// ���������� ���������� ������
		counter = 0;
		// Interactor.Trace("Start downloading stories.");
		// Interactor.Notification(NOTIFICATION_CURRENT,0,0);

		// var amount = Interactor.GetAllLinksAmount();
		// Interactor.Print("Found "+amount+" new storie(s) in all plugins.");
	}
	Interactor.Start(id);
};

function Finish() {
	// var i=Interactor.GetDownloadedCounter();
	// FeederAPI.Trace("Downloaded "+i+" storie(s).");
	// if(i>0){
	// FeederAPI.Notification(NOTIFICATION_FINISH,0,i);
	// }else{
	// FeederAPI.Notification(NOTIFICATION_CANCEL,0,0);
	// }
	Interactor.Finish();
};

function api_done(str) {// called always after custom method finishes
	Interactor.done(str);
};

function api_onload() {// called on finish page loading
	Interactor.loaded();
};
