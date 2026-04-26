# 智慧乡村 smartVillages API（阅读优化版）

## 基本信息

**简介**:智慧乡村 smartVillages API
**HOST**:http://localhost:8080
**Version**:v1
**接口路径**:/v3/api-docs

## 总目录

- 管理员接口（5）
- 村民留言（9）
- 村务管理-党建组织信息（5）
- 村务管理-房屋与土地台账（5）
- 权限管理（2）
- 乡村风采（9）
- 村务管理-人口台账（5）
- 媒体资源（5）
- 村务管理-民生服务（10）
- 公告接口（12）
- 村务管理-村务事项-公示（8）

---

## 模块：管理员接口
<a id="模块-管理员接口"></a>



<details>
<summary><strong>启用-禁用用户</strong></summary>



**接口地址**:`/admin/users/{id}/status`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||
|status||query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>创建村干部</strong></summary>



**接口地址**:`/admin/users/cadre`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "username": "",
  "password": "",
  "phone": "",
  "avatar": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|authDTO|用户认证DTO|body|true|AuthDTO|AuthDTO|
|&emsp;&emsp;username|用户名||true|string||
|&emsp;&emsp;password|密码||true|string||
|&emsp;&emsp;phone|手机号||true|string||
|&emsp;&emsp;avatar|头像||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultCreateCaderVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||CreateCaderVO|CreateCaderVO|
|&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;avatar|头像|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"username": "",
		"avatar": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>上传头像</strong></summary>



**接口地址**:`/admin/users/cadre/avatar`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,multipart/form-data`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|avatar||query|false|file||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>分页查询用户</strong></summary>



**接口地址**:`/admin/users`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|username||query|false|string||
|role||query|false|string||
|status||query|false|integer(int32)||
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageAuthVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageAuthVO|IPageAuthVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|AuthVO|
|&emsp;&emsp;&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;&emsp;&emsp;role|角色|string||
|&emsp;&emsp;&emsp;&emsp;phone|手机号|string||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;avatar|头像 URL|string||
|&emsp;&emsp;&emsp;&emsp;status|状态：0-禁用 1-启用|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;updateTime|更新时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"username": "",
				"role": "",
				"phone": "",
				"createTime": "",
				"avatar": "",
				"status": 0,
				"updateTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>me</strong></summary>



**接口地址**:`/admin/me`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMeVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||MeVO|MeVO|
|&emsp;&emsp;id|用户ID|integer(int32)||
|&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;role|角色|string||
|&emsp;&emsp;avatar|头像|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"username": "",
		"role": "",
		"avatar": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


## 模块：村民留言
<a id="模块-村民留言"></a>



<details>
<summary><strong>回复村民留言</strong></summary>



**接口地址**:`/cadre/interactions/messages/{id}/replies`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "reply": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|replyInteractionDTO|回复村民留言DTO|body|true|ReplyInteractionDTO|ReplyInteractionDTO|
|&emsp;&emsp;reply|回复内容||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>获取村民留言列表</strong></summary>



**接口地址**:`/interactions/messages`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageInteractionCreateVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageInteractionCreateVO|IPageInteractionCreateVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|InteractionCreateVO|
|&emsp;&emsp;&emsp;&emsp;userId|留言者id|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;content|留言内容|string||
|&emsp;&emsp;&emsp;&emsp;type|类型：consult-咨询/complaint-投诉/suggest-建议|string||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"userId": 0,
				"content": "",
				"type": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>新增村民留言</strong></summary>



**接口地址**:`/interactions/messages`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "content": "",
  "type": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|interactionCreateDTO|村民留言创建DTO|body|true|InteractionCreateDTO|InteractionCreateDTO|
|&emsp;&emsp;content|留言内容||true|string||
|&emsp;&emsp;type|类型：consult-咨询/complaint-投诉/suggest-建议||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultInteractionCreateVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||InteractionCreateVO|InteractionCreateVO|
|&emsp;&emsp;userId|留言者id|integer(int32)||
|&emsp;&emsp;content|留言内容|string||
|&emsp;&emsp;type|类型：consult-咨询/complaint-投诉/suggest-建议|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"userId": 0,
		"content": "",
		"type": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>村民撤回留言</strong></summary>



**接口地址**:`/interactions/messages/my/{id}/withdraw`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>管理端处理村民留言</strong></summary>



**接口地址**:`/cadre/interactions/messages/{id}/processing`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>我的留言</strong></summary>



**接口地址**:`/interactions/messages/my`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageInteractionDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageInteractionDetailVO|IPageInteractionDetailVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|InteractionDetailVO|
|&emsp;&emsp;&emsp;&emsp;id|留言id|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;userId|留言者id|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;content|留言内容|string||
|&emsp;&emsp;&emsp;&emsp;type|类型：consult-咨询/complaint-投诉/suggest-建议|string||
|&emsp;&emsp;&emsp;&emsp;status|状态：0-待处理 1-处理中 2-已回复 3-已关闭|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;reply|官方回复|string||
|&emsp;&emsp;&emsp;&emsp;replyTime|回复时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;replyUser|回复人id|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"userId": 0,
				"content": "",
				"type": "",
				"status": 0,
				"reply": "",
				"replyTime": "",
				"replyUser": 0,
				"createTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>我的留言详细</strong></summary>



**接口地址**:`/interactions/messages/my/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultInteractionDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||InteractionDetailVO|InteractionDetailVO|
|&emsp;&emsp;id|留言id|integer(int32)||
|&emsp;&emsp;userId|留言者id|integer(int32)||
|&emsp;&emsp;content|留言内容|string||
|&emsp;&emsp;type|类型：consult-咨询/complaint-投诉/suggest-建议|string||
|&emsp;&emsp;status|状态：0-待处理 1-处理中 2-已回复 3-已关闭|integer(int32)||
|&emsp;&emsp;reply|官方回复|string||
|&emsp;&emsp;replyTime|回复时间|string(date-time)||
|&emsp;&emsp;replyUser|回复人id|integer(int32)||
|&emsp;&emsp;createTime|创建时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"userId": 0,
		"content": "",
		"type": "",
		"status": 0,
		"reply": "",
		"replyTime": "",
		"replyUser": 0,
		"createTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>管理端获取村民留言列表</strong></summary>



**接口地址**:`/cadre/interactions/messages`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|status||query|false|integer(int32)||
|type||query|false|string||
|startTime||query|false|string(date-time)||
|endTime||query|false|string(date-time)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageInteractionDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageInteractionDetailVO|IPageInteractionDetailVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|InteractionDetailVO|
|&emsp;&emsp;&emsp;&emsp;id|留言id|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;userId|留言者id|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;content|留言内容|string||
|&emsp;&emsp;&emsp;&emsp;type|类型：consult-咨询/complaint-投诉/suggest-建议|string||
|&emsp;&emsp;&emsp;&emsp;status|状态：0-待处理 1-处理中 2-已回复 3-已关闭|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;reply|官方回复|string||
|&emsp;&emsp;&emsp;&emsp;replyTime|回复时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;replyUser|回复人id|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"userId": 0,
				"content": "",
				"type": "",
				"status": 0,
				"reply": "",
				"replyTime": "",
				"replyUser": 0,
				"createTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>获取村民留言详情</strong></summary>



**接口地址**:`/cadre/interactions/messages/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultInteractionDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||InteractionDetailVO|InteractionDetailVO|
|&emsp;&emsp;id|留言id|integer(int32)||
|&emsp;&emsp;userId|留言者id|integer(int32)||
|&emsp;&emsp;content|留言内容|string||
|&emsp;&emsp;type|类型：consult-咨询/complaint-投诉/suggest-建议|string||
|&emsp;&emsp;status|状态：0-待处理 1-处理中 2-已回复 3-已关闭|integer(int32)||
|&emsp;&emsp;reply|官方回复|string||
|&emsp;&emsp;replyTime|回复时间|string(date-time)||
|&emsp;&emsp;replyUser|回复人id|integer(int32)||
|&emsp;&emsp;createTime|创建时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"userId": 0,
		"content": "",
		"type": "",
		"status": 0,
		"reply": "",
		"replyTime": "",
		"replyUser": 0,
		"createTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


## 模块：村务管理-党建组织信息
<a id="模块-村务管理-党建组织信息"></a>



<details>
<summary><strong>根据id获取党建组织信息详情</strong></summary>



**接口地址**:`/cadre/village-party/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVillagePartyDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||VillagePartyDetailVO|VillagePartyDetailVO|
|&emsp;&emsp;id|主键|integer(int32)||
|&emsp;&emsp;orgName|党组织名称|string||
|&emsp;&emsp;orgType|组织类型：党支部/党总支|string||
|&emsp;&emsp;secretaryName|书记姓名|string||
|&emsp;&emsp;memberCount|党员人数|integer(int32)||
|&emsp;&emsp;contactPhone|联系电话|string||
|&emsp;&emsp;remark|备注|string||
|&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;updateTime|更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"orgName": "",
		"orgType": "",
		"secretaryName": "",
		"memberCount": 0,
		"contactPhone": "",
		"remark": "",
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>更新党建组织信息</strong></summary>



**接口地址**:`/cadre/village-party/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "orgName": "",
  "orgType": "",
  "secretaryName": "",
  "memberCount": 0,
  "contactPhone": "",
  "remark": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||
|villagePartyUpdateDTO|党建组织信息更新DTO|body|true|VillagePartyUpdateDTO|VillagePartyUpdateDTO|
|&emsp;&emsp;orgName|党组织名称||true|string||
|&emsp;&emsp;orgType|组织类型：党支部/党总支||false|string||
|&emsp;&emsp;secretaryName|书记姓名||false|string||
|&emsp;&emsp;memberCount|党员人数||false|integer(int32)||
|&emsp;&emsp;contactPhone|联系电话||false|string||
|&emsp;&emsp;remark|备注||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>删除党建组织信息</strong></summary>



**接口地址**:`/cadre/village-party/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>分页查询党建组织信息列表</strong></summary>



**接口地址**:`/cadre/village-party`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|orgName||query|false|string||
|orgType||query|false|string||
|secretaryName||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageVillagePartySimpleVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageVillagePartySimpleVO|IPageVillagePartySimpleVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|VillagePartySimpleVO|
|&emsp;&emsp;&emsp;&emsp;id|主键|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;orgName|党组织名称|string||
|&emsp;&emsp;&emsp;&emsp;orgType|组织类型：党支部/党总支|string||
|&emsp;&emsp;&emsp;&emsp;secretaryName|书记姓名|string||
|&emsp;&emsp;&emsp;&emsp;memberCount|党员人数|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"orgName": "",
				"orgType": "",
				"secretaryName": "",
				"memberCount": 0,
				"createTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>创建党建组织信息</strong></summary>



**接口地址**:`/cadre/village-party`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "orgName": "",
  "orgType": "",
  "secretaryName": "",
  "memberCount": 0,
  "contactPhone": "",
  "remark": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|villagePartyCreateDTO|党建组织信息创建DTO|body|true|VillagePartyCreateDTO|VillagePartyCreateDTO|
|&emsp;&emsp;orgName|党组织名称||true|string||
|&emsp;&emsp;orgType|组织类型：党支部/党总支||false|string||
|&emsp;&emsp;secretaryName|书记姓名||false|string||
|&emsp;&emsp;memberCount|党员人数||false|integer(int32)||
|&emsp;&emsp;contactPhone|联系电话||false|string||
|&emsp;&emsp;remark|备注||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultInteger|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": 0
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


## 模块：村务管理-房屋与土地台账
<a id="模块-村务管理-房屋与土地台账"></a>



<details>
<summary><strong>根据id获取房屋与土地台账详情</strong></summary>



**接口地址**:`/cadre/village-house-land/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVillageHouseLandDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||VillageHouseLandDetailVO|VillageHouseLandDetailVO|
|&emsp;&emsp;id|主键|integer(int32)||
|&emsp;&emsp;bizType|类型：HOUSE房屋 LAND土地|string||
|&emsp;&emsp;parcelCode|地块/房屋编号|string||
|&emsp;&emsp;location|坐落|string||
|&emsp;&emsp;areaMu|面积（亩）|number||
|&emsp;&emsp;ownerName|权利人/户主|string||
|&emsp;&emsp;certNo|权证号|string||
|&emsp;&emsp;remark|备注|string||
|&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;updateTime|更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"bizType": "",
		"parcelCode": "",
		"location": "",
		"areaMu": 0,
		"ownerName": "",
		"certNo": "",
		"remark": "",
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>更新房屋与土地台账</strong></summary>



**接口地址**:`/cadre/village-house-land/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "bizType": "",
  "parcelCode": "",
  "location": "",
  "areaMu": 0,
  "ownerName": "",
  "certNo": "",
  "remark": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||
|villageHouseLandUpdateDTO|房屋与土地台账更新DTO|body|true|VillageHouseLandUpdateDTO|VillageHouseLandUpdateDTO|
|&emsp;&emsp;bizType|类型：HOUSE房屋 LAND土地||true|string||
|&emsp;&emsp;parcelCode|地块/房屋编号||false|string||
|&emsp;&emsp;location|坐落||false|string||
|&emsp;&emsp;areaMu|面积（亩）||false|number||
|&emsp;&emsp;ownerName|权利人/户主||false|string||
|&emsp;&emsp;certNo|权证号||false|string||
|&emsp;&emsp;remark|备注||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>删除房屋与土地台账</strong></summary>



**接口地址**:`/cadre/village-house-land/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>分页查询房屋与土地台账列表</strong></summary>



**接口地址**:`/cadre/village-house-land`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|bizType||query|false|string||
|ownerName||query|false|string||
|location||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageVillageHouseLandSimpleVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageVillageHouseLandSimpleVO|IPageVillageHouseLandSimpleVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|VillageHouseLandSimpleVO|
|&emsp;&emsp;&emsp;&emsp;id|主键|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;bizType|类型：HOUSE房屋 LAND土地|string||
|&emsp;&emsp;&emsp;&emsp;parcelCode|地块/房屋编号|string||
|&emsp;&emsp;&emsp;&emsp;location|坐落|string||
|&emsp;&emsp;&emsp;&emsp;areaMu|面积（亩）|number||
|&emsp;&emsp;&emsp;&emsp;ownerName|权利人/户主|string||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"bizType": "",
				"parcelCode": "",
				"location": "",
				"areaMu": 0,
				"ownerName": "",
				"createTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>创建房屋与土地台账</strong></summary>



**接口地址**:`/cadre/village-house-land`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "bizType": "",
  "parcelCode": "",
  "location": "",
  "areaMu": 0,
  "ownerName": "",
  "certNo": "",
  "remark": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|villageHouseLandCreateDTO|房屋与土地台账创建DTO|body|true|VillageHouseLandCreateDTO|VillageHouseLandCreateDTO|
|&emsp;&emsp;bizType|类型：HOUSE房屋 LAND土地||true|string||
|&emsp;&emsp;parcelCode|地块/房屋编号||false|string||
|&emsp;&emsp;location|坐落||false|string||
|&emsp;&emsp;areaMu|面积（亩）||false|number||
|&emsp;&emsp;ownerName|权利人/户主||false|string||
|&emsp;&emsp;certNo|权证号||false|string||
|&emsp;&emsp;remark|备注||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultInteger|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": 0
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


## 模块：权限管理
<a id="模块-权限管理"></a>



<details>
<summary><strong>登录</strong></summary>



**接口地址**:`/auth/login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "username": "",
  "password": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|loginRequest|登录请求|body|true|LoginRequest|LoginRequest|
|&emsp;&emsp;username|用户名||true|string||
|&emsp;&emsp;password|密码（明文）||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultJwtResponse|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||JwtResponse|JwtResponse|
|&emsp;&emsp;id|用户id|integer(int32)||
|&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;role|角色|string||
|&emsp;&emsp;avatar|头像|string||
|&emsp;&emsp;token|访问令牌|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"username": "",
		"role": "",
		"avatar": "",
		"token": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>登出</strong></summary>



**接口地址**:`/auth/logout`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


## 模块：乡村风采
<a id="模块-乡村风采"></a>



<details>
<summary><strong>修改乡村风采</strong></summary>



**接口地址**:`/cadre/features/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "title": "",
  "content": "",
  "cover": "",
  "video": "",
  "images": "",
  "type": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|highlightCreateDTO|乡村风采创建DTO|body|true|HighlightCreateDTO|HighlightCreateDTO|
|&emsp;&emsp;title|标题||true|string||
|&emsp;&emsp;content|内容||true|string||
|&emsp;&emsp;cover|封面||true|string||
|&emsp;&emsp;video|视频||false|string||
|&emsp;&emsp;images|图片||false|string||
|&emsp;&emsp;type|类型||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>删除乡村风采</strong></summary>



**接口地址**:`/cadre/features/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>上下架乡村风采</strong></summary>



**接口地址**:`/cadre/features/{id}/status`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|status||query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>管理端获取乡村风采列表</strong></summary>



**接口地址**:`/cadre/features`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|status||query|false|integer(int32)||
|title||query|false|string||
|type||query|false|string||
|getSort||query|false|integer(int32)||
|getCreateTime||query|false|string(date-time)||
|startTime||query|false|string(date-time)||
|endTime||query|false|string(date-time)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageFeatureVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageFeatureVO|IPageFeatureVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|FeatureVO|
|&emsp;&emsp;&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;title|标题|string||
|&emsp;&emsp;&emsp;&emsp;content|内容|string||
|&emsp;&emsp;&emsp;&emsp;type|类型|string||
|&emsp;&emsp;&emsp;&emsp;cover|封面|string||
|&emsp;&emsp;&emsp;&emsp;video|视频|string||
|&emsp;&emsp;&emsp;&emsp;images|图片|string||
|&emsp;&emsp;&emsp;&emsp;createUser|创建用户|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;updateTime|更新时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"title": "",
				"content": "",
				"type": "",
				"cover": "",
				"video": "",
				"images": "",
				"createUser": 0,
				"createTime": "",
				"updateTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>村干部创建乡村风采</strong></summary>



**接口地址**:`/cadre/features`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "title": "",
  "content": "",
  "cover": "",
  "video": "",
  "images": "",
  "type": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|highlightCreateDTO|乡村风采创建DTO|body|true|HighlightCreateDTO|HighlightCreateDTO|
|&emsp;&emsp;title|标题||true|string||
|&emsp;&emsp;content|内容||true|string||
|&emsp;&emsp;cover|封面||true|string||
|&emsp;&emsp;video|视频||false|string||
|&emsp;&emsp;images|图片||false|string||
|&emsp;&emsp;type|类型||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>获取乡村风采列表(村民可见)</strong></summary>



**接口地址**:`/features`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|type||query|false|string||
|getSort||query|false|integer(int32)||
|getCreateTime||query|false|string(date-time)||
|startTime||query|false|string(date-time)||
|endTime||query|false|string(date-time)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageFeatureVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageFeatureVO|IPageFeatureVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|FeatureVO|
|&emsp;&emsp;&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;title|标题|string||
|&emsp;&emsp;&emsp;&emsp;content|内容|string||
|&emsp;&emsp;&emsp;&emsp;type|类型|string||
|&emsp;&emsp;&emsp;&emsp;cover|封面|string||
|&emsp;&emsp;&emsp;&emsp;video|视频|string||
|&emsp;&emsp;&emsp;&emsp;images|图片|string||
|&emsp;&emsp;&emsp;&emsp;createUser|创建用户|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;updateTime|更新时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"title": "",
				"content": "",
				"type": "",
				"cover": "",
				"video": "",
				"images": "",
				"createUser": 0,
				"createTime": "",
				"updateTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>获取乡村风采详情</strong></summary>



**接口地址**:`/features/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultFeatureVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||FeatureVO|FeatureVO|
|&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;title|标题|string||
|&emsp;&emsp;content|内容|string||
|&emsp;&emsp;type|类型|string||
|&emsp;&emsp;cover|封面|string||
|&emsp;&emsp;video|视频|string||
|&emsp;&emsp;images|图片|string||
|&emsp;&emsp;createUser|创建用户|integer(int32)||
|&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;updateTime|更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"title": "",
		"content": "",
		"type": "",
		"cover": "",
		"video": "",
		"images": "",
		"createUser": 0,
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>获取乡村风采分类统计</strong></summary>



**接口地址**:`/features/statistics`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringLong|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>村民获取我的乡村风采</strong></summary>



**接口地址**:`/cadre/features/summary`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringLong|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


## 模块：村务管理-人口台账
<a id="模块-村务管理-人口台账"></a>



<details>
<summary><strong>根据id获取人口台账详情</strong></summary>



**接口地址**:`/cadre/village-population/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVillagePopulationDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||VillagePopulationDetailVO|VillagePopulationDetailVO|
|&emsp;&emsp;id|主键|integer(int32)||
|&emsp;&emsp;householdNo|户号|string||
|&emsp;&emsp;fullName|姓名|string||
|&emsp;&emsp;gender|性别：0未知 1男 2女|integer(int32)||
|&emsp;&emsp;birthDate|出生日期|string(date)||
|&emsp;&emsp;idCardLast4|身份证后四位|string||
|&emsp;&emsp;relationToHead|与户主关系|string||
|&emsp;&emsp;address|地址|string||
|&emsp;&emsp;remark|备注|string||
|&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;updateTime|更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"householdNo": "",
		"fullName": "",
		"gender": 0,
		"birthDate": "",
		"idCardLast4": "",
		"relationToHead": "",
		"address": "",
		"remark": "",
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>更新人口台账</strong></summary>



**接口地址**:`/cadre/village-population/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "householdNo": "",
  "fullName": "",
  "gender": 0,
  "birthDate": "",
  "idCardLast4": "",
  "relationToHead": "",
  "address": "",
  "remark": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|villagePopulationUpdateDTO|人口台账更新DTO|body|true|VillagePopulationUpdateDTO|VillagePopulationUpdateDTO|
|&emsp;&emsp;householdNo|户号||false|string||
|&emsp;&emsp;fullName|姓名||true|string||
|&emsp;&emsp;gender|性别：0未知 1男 2女||false|integer(int32)||
|&emsp;&emsp;birthDate|出生日期||false|string(date)||
|&emsp;&emsp;idCardLast4|身份证后四位||false|string||
|&emsp;&emsp;relationToHead|与户主关系||false|string||
|&emsp;&emsp;address|地址||false|string||
|&emsp;&emsp;remark|备注||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>删除人口台账</strong></summary>



**接口地址**:`/cadre/village-population/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>分页查询人口台账列表</strong></summary>



**接口地址**:`/cadre/village-population`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|householdNo||query|false|string||
|fullName||query|false|string||
|gender||query|false|integer(int32)||
|relationToHead||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageVillagePopulationSimpleVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageVillagePopulationSimpleVO|IPageVillagePopulationSimpleVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|VillagePopulationSimpleVO|
|&emsp;&emsp;&emsp;&emsp;id|主键|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;householdNo|户号|string||
|&emsp;&emsp;&emsp;&emsp;fullName|姓名|string||
|&emsp;&emsp;&emsp;&emsp;gender|性别：0未知 1男 2女|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;address|地址|string||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"householdNo": "",
				"fullName": "",
				"gender": 0,
				"address": "",
				"createTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>创建人口台账</strong></summary>



**接口地址**:`/cadre/village-population`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "householdNo": "",
  "fullName": "",
  "gender": 0,
  "birthDate": "",
  "idCardLast4": "",
  "relationToHead": "",
  "address": "",
  "remark": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|villagePopulationCreateDTO|人口台账创建DTO|body|true|VillagePopulationCreateDTO|VillagePopulationCreateDTO|
|&emsp;&emsp;householdNo|户号||false|string||
|&emsp;&emsp;fullName|姓名||true|string||
|&emsp;&emsp;gender|性别：0未知 1男 2女||false|integer(int32)||
|&emsp;&emsp;birthDate|出生日期||false|string(date)||
|&emsp;&emsp;idCardLast4|身份证后四位||false|string||
|&emsp;&emsp;relationToHead|与户主关系||false|string||
|&emsp;&emsp;address|地址||false|string||
|&emsp;&emsp;remark|备注||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


## 模块：媒体资源
<a id="模块-媒体资源"></a>



<details>
<summary><strong>启用-禁用媒体资源</strong></summary>



**接口地址**:`/media/cadre/{id}/status`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||
|status||query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>上传文件</strong></summary>



**接口地址**:`/media/upload`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,multipart/form-data`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|fileType||query|true|string||
|category||query|true|string||
|file||query|true|file||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultUploadVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||UploadVO|UploadVO|
|&emsp;&emsp;fileName|文件名|string||
|&emsp;&emsp;fileSize|文件大小|integer(int64)||
|&emsp;&emsp;fileUrl|文件URL|string||
|&emsp;&emsp;objectKey|对象键|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"fileName": "",
		"fileSize": 0,
		"fileUrl": "",
		"objectKey": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>获取媒体资源详情</strong></summary>



**接口地址**:`/media/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||DetailVO|DetailVO|
|&emsp;&emsp;id|媒体资源id|integer(int32)||
|&emsp;&emsp;fileName|文件名|string||
|&emsp;&emsp;fileUrl|文件URL|string||
|&emsp;&emsp;fileType|文件类型|string||
|&emsp;&emsp;fileSize|文件大小|integer(int64)||
|&emsp;&emsp;category|分类|string||
|&emsp;&emsp;uploadUser|上传用户|integer(int32)||
|&emsp;&emsp;status|状态|integer(int32)||
|&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;updateTime|更新时间|string(date-time)||
|&emsp;&emsp;deleted|逻辑删除|integer(int32)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"fileName": "",
		"fileUrl": "",
		"fileType": "",
		"fileSize": 0,
		"category": "",
		"uploadUser": 0,
		"status": 0,
		"createTime": "",
		"updateTime": "",
		"deleted": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>分页查询媒体资源</strong></summary>



**接口地址**:`/media/page`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|fileType||query|false|string||
|category||query|false|string||
|status||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPagePageVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPagePageVO|IPagePageVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|PageVO|
|&emsp;&emsp;&emsp;&emsp;id|媒体资源id|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;fileName|文件名|string||
|&emsp;&emsp;&emsp;&emsp;fileUrl|文件URL|string||
|&emsp;&emsp;&emsp;&emsp;fileSize|文件大小|integer(int64)||
|&emsp;&emsp;&emsp;&emsp;category|分类|string||
|&emsp;&emsp;&emsp;&emsp;uploadUser|上传用户|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;status|状态|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;updateTime|更新时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"fileName": "",
				"fileUrl": "",
				"fileSize": 0,
				"category": "",
				"uploadUser": 0,
				"status": 0,
				"createTime": "",
				"updateTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>删除媒体资源</strong></summary>



**接口地址**:`/media/cadre/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


## 模块：村务管理-民生服务
<a id="模块-村务管理-民生服务"></a>



<details>
<summary><strong>取消我的民生服务工单申请</strong></summary>



**接口地址**:`/villager/management/services/my/{id}/close`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>管理端处理民生服务工单申请</strong></summary>



**接口地址**:`/cadre/management/services/{id}/processing`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "handleNote": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|serviceTicketDoneDTO|民生服务工单办结DTO|body|true|ServiceTicketDoneDTO|ServiceTicketDoneDTO|
|&emsp;&emsp;handleNote|处理说明||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>管理端办结民生服务工单申请</strong></summary>



**接口地址**:`/cadre/management/services/{id}/done`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>管理端关闭工单</strong></summary>



**接口地址**:`/cadre/management/services/{id}/close`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>村民提交民生服务工单</strong></summary>



**接口地址**:`/villager/management/services`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "serviceType": "",
  "title": "",
  "detail": "",
  "contactPhone": "",
  "attachments": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|serviceTicketCreateDTO|民生服务工单创建DTO|body|true|ServiceTicketCreateDTO|ServiceTicketCreateDTO|
|&emsp;&emsp;serviceType|类型：subsidy/employment/medical/dispute/other||true|string||
|&emsp;&emsp;title|标题||true|string||
|&emsp;&emsp;detail|详细描述||false|string||
|&emsp;&emsp;contactPhone|联系电话||false|string||
|&emsp;&emsp;attachments|附件（JSON数组字符串）||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultInteger|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": 0
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>获取民生服务工单列表</strong></summary>



**接口地址**:`/villager/management/services/my`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|serviceType||query|false|string||
|status||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageServiceTicketSimpleVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageServiceTicketSimpleVO|IPageServiceTicketSimpleVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|ServiceTicketSimpleVO|
|&emsp;&emsp;&emsp;&emsp;id|民生服务工单id|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;serviceType|民生服务工单类型|string||
|&emsp;&emsp;&emsp;&emsp;title|民生服务工单标题|string||
|&emsp;&emsp;&emsp;&emsp;status|民生服务工单状态|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|民生服务工单创建时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"serviceType": "",
				"title": "",
				"status": 0,
				"createTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>获取我的民生服务工单详情</strong></summary>



**接口地址**:`/villager/management/services/my/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultServiceTicketDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||ServiceTicketDetailVO|ServiceTicketDetailVO|
|&emsp;&emsp;id|民生服务工单id|integer(int32)||
|&emsp;&emsp;serviceType|民生服务工单类型|string||
|&emsp;&emsp;title|民生服务工单标题|string||
|&emsp;&emsp;detail|民生服务工单详细描述|string||
|&emsp;&emsp;contactPhone|民生服务工单联系电话|string||
|&emsp;&emsp;attachments|民生服务工单附件|string||
|&emsp;&emsp;status|民生服务工单状态|integer(int32)||
|&emsp;&emsp;applicantId|民生服务工单申请人id|integer(int32)||
|&emsp;&emsp;handlerId|民生服务工单处理人id|integer(int32)||
|&emsp;&emsp;handleNote|民生服务工单处理说明|string||
|&emsp;&emsp;handleTime|民生服务工单处理时间|string(date-time)||
|&emsp;&emsp;createTime|民生服务工单创建时间|string(date-time)||
|&emsp;&emsp;updateTime|民生服务工单更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"serviceType": "",
		"title": "",
		"detail": "",
		"contactPhone": "",
		"attachments": "",
		"status": 0,
		"applicantId": 0,
		"handlerId": 0,
		"handleNote": "",
		"handleTime": "",
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>管理端获取民生服务工单列表</strong></summary>



**接口地址**:`/cadre/management/services`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|serviceType||query|false|string||
|status||query|false|integer(int32)||
|starTime||query|false|string(date-time)||
|endTime||query|false|string(date-time)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageServiceTicketSimpleVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageServiceTicketSimpleVO|IPageServiceTicketSimpleVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|ServiceTicketSimpleVO|
|&emsp;&emsp;&emsp;&emsp;id|民生服务工单id|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;serviceType|民生服务工单类型|string||
|&emsp;&emsp;&emsp;&emsp;title|民生服务工单标题|string||
|&emsp;&emsp;&emsp;&emsp;status|民生服务工单状态|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|民生服务工单创建时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"serviceType": "",
				"title": "",
				"status": 0,
				"createTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>管理端获取民生服务工单详情</strong></summary>



**接口地址**:`/cadre/management/services/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultServiceTicketDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||ServiceTicketDetailVO|ServiceTicketDetailVO|
|&emsp;&emsp;id|民生服务工单id|integer(int32)||
|&emsp;&emsp;serviceType|民生服务工单类型|string||
|&emsp;&emsp;title|民生服务工单标题|string||
|&emsp;&emsp;detail|民生服务工单详细描述|string||
|&emsp;&emsp;contactPhone|民生服务工单联系电话|string||
|&emsp;&emsp;attachments|民生服务工单附件|string||
|&emsp;&emsp;status|民生服务工单状态|integer(int32)||
|&emsp;&emsp;applicantId|民生服务工单申请人id|integer(int32)||
|&emsp;&emsp;handlerId|民生服务工单处理人id|integer(int32)||
|&emsp;&emsp;handleNote|民生服务工单处理说明|string||
|&emsp;&emsp;handleTime|民生服务工单处理时间|string(date-time)||
|&emsp;&emsp;createTime|民生服务工单创建时间|string(date-time)||
|&emsp;&emsp;updateTime|民生服务工单更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"serviceType": "",
		"title": "",
		"detail": "",
		"contactPhone": "",
		"attachments": "",
		"status": 0,
		"applicantId": 0,
		"handlerId": 0,
		"handleNote": "",
		"handleTime": "",
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>后台申请统计</strong></summary>



**接口地址**:`/cadre/management/services/statistics`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringLong|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


## 模块：公告接口
<a id="模块-公告接口"></a>



<details>
<summary><strong>村干部公告详情</strong></summary>



**接口地址**:`/cadre/announcements/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultAnnouncementVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||AnnouncementVO|AnnouncementVO|
|&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;title|标题|string||
|&emsp;&emsp;content|内容|string||
|&emsp;&emsp;status|状态|integer(int32)||
|&emsp;&emsp;type|类型|integer(int32)||
|&emsp;&emsp;isTop|是否置顶|integer(int32)||
|&emsp;&emsp;publishTime|发布时间|string(date-time)||
|&emsp;&emsp;auditTime|审核时间|string(date-time)||
|&emsp;&emsp;auditUser|审核人|integer(int32)||
|&emsp;&emsp;viewCount|浏览次数|integer(int32)||
|&emsp;&emsp;createUser|创建人|integer(int32)||
|&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;updateTime|更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"title": "",
		"content": "",
		"status": 0,
		"type": 0,
		"isTop": 0,
		"publishTime": "",
		"auditTime": "",
		"auditUser": 0,
		"viewCount": 0,
		"createUser": 0,
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>编辑公告基础信息</strong></summary>



**接口地址**:`/cadre/announcements/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "title": "",
  "content": "",
  "type": 0,
  "isTop": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|announcementUpdateDTO|公告更新 DTO|body|true|AnnouncementUpdateDTO|AnnouncementUpdateDTO|
|&emsp;&emsp;title|标题||true|string||
|&emsp;&emsp;content|内容||true|string||
|&emsp;&emsp;type|类型：1-通知 2-公告 3-公示||true|integer(int32)||
|&emsp;&emsp;isTop|是否置顶：0-否 1-是||true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>删除公告</strong></summary>



**接口地址**:`/cadre/announcements/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>上架-下架公告</strong></summary>



**接口地址**:`/cadre/announcements/{id}/status`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|status||query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>村干部审核公告</strong></summary>



**接口地址**:`/cadre/announcements/{id}/audit`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|status||query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>村干部分页查询公告</strong></summary>



**接口地址**:`/cadre/announcements`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|status||query|false|integer(int32)||
|title||query|false|string||
|type||query|false|integer(int32)||
|isTop||query|false|integer(int32)||
|startTime||query|false|string(date-time)||
|endTime||query|false|string(date-time)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageAnnouncementVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageAnnouncementVO|IPageAnnouncementVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|AnnouncementVO|
|&emsp;&emsp;&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;title|标题|string||
|&emsp;&emsp;&emsp;&emsp;content|内容|string||
|&emsp;&emsp;&emsp;&emsp;status|状态|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;type|类型|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;isTop|是否置顶|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;publishTime|发布时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;auditTime|审核时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;auditUser|审核人|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;viewCount|浏览次数|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createUser|创建人|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;updateTime|更新时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"title": "",
				"content": "",
				"status": 0,
				"type": 0,
				"isTop": 0,
				"publishTime": "",
				"auditTime": "",
				"auditUser": 0,
				"viewCount": 0,
				"createUser": 0,
				"createTime": "",
				"updateTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>村干部新增公告</strong></summary>



**接口地址**:`/cadre/announcements`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "title": "",
  "content": "",
  "type": 0,
  "isTop": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|announcementCreateDTO|公告创建 DTO|body|true|AnnouncementCreateDTO|AnnouncementCreateDTO|
|&emsp;&emsp;title|标题||true|string||
|&emsp;&emsp;content|内容||true|string||
|&emsp;&emsp;type|类型：1-通知 2-公告 3-公示||true|integer(int32)||
|&emsp;&emsp;isTop|是否置顶：0-否 1-是||true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>村干部待审核公告</strong></summary>



**接口地址**:`/cadre/announcements/pending`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|title||query|false|string||
|type||query|false|integer(int32)||
|isTop||query|false|integer(int32)||
|startTime||query|false|string(date-time)||
|endTime||query|false|string(date-time)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageAnnouncementVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageAnnouncementVO|IPageAnnouncementVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|AnnouncementVO|
|&emsp;&emsp;&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;title|标题|string||
|&emsp;&emsp;&emsp;&emsp;content|内容|string||
|&emsp;&emsp;&emsp;&emsp;status|状态|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;type|类型|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;isTop|是否置顶|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;publishTime|发布时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;auditTime|审核时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;auditUser|审核人|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;viewCount|浏览次数|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createUser|创建人|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;updateTime|更新时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"title": "",
				"content": "",
				"status": 0,
				"type": 0,
				"isTop": 0,
				"publishTime": "",
				"auditTime": "",
				"auditUser": 0,
				"viewCount": 0,
				"createUser": 0,
				"createTime": "",
				"updateTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>审核历史列表</strong></summary>



**接口地址**:`/cadre/announcements/audited`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|title||query|false|string||
|type||query|false|integer(int32)||
|isTop||query|false|integer(int32)||
|startTime||query|false|string(date-time)||
|endTime||query|false|string(date-time)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageAnnouncementVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageAnnouncementVO|IPageAnnouncementVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|AnnouncementVO|
|&emsp;&emsp;&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;title|标题|string||
|&emsp;&emsp;&emsp;&emsp;content|内容|string||
|&emsp;&emsp;&emsp;&emsp;status|状态|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;type|类型|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;isTop|是否置顶|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;publishTime|发布时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;auditTime|审核时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;auditUser|审核人|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;viewCount|浏览次数|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createUser|创建人|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;updateTime|更新时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"title": "",
				"content": "",
				"status": 0,
				"type": 0,
				"isTop": 0,
				"publishTime": "",
				"auditTime": "",
				"auditUser": 0,
				"viewCount": 0,
				"createUser": 0,
				"createTime": "",
				"updateTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>前台分页公告（仅已发布）</strong></summary>



**接口地址**:`/announcements`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageAnnouncementVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageAnnouncementVO|IPageAnnouncementVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|AnnouncementVO|
|&emsp;&emsp;&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;title|标题|string||
|&emsp;&emsp;&emsp;&emsp;content|内容|string||
|&emsp;&emsp;&emsp;&emsp;status|状态|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;type|类型|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;isTop|是否置顶|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;publishTime|发布时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;auditTime|审核时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;auditUser|审核人|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;viewCount|浏览次数|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createUser|创建人|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;updateTime|更新时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"title": "",
				"content": "",
				"status": 0,
				"type": 0,
				"isTop": 0,
				"publishTime": "",
				"auditTime": "",
				"auditUser": 0,
				"viewCount": 0,
				"createUser": 0,
				"createTime": "",
				"updateTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>公告详细</strong></summary>



**接口地址**:`/announcements/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultAnnouncementVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||AnnouncementVO|AnnouncementVO|
|&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;title|标题|string||
|&emsp;&emsp;content|内容|string||
|&emsp;&emsp;status|状态|integer(int32)||
|&emsp;&emsp;type|类型|integer(int32)||
|&emsp;&emsp;isTop|是否置顶|integer(int32)||
|&emsp;&emsp;publishTime|发布时间|string(date-time)||
|&emsp;&emsp;auditTime|审核时间|string(date-time)||
|&emsp;&emsp;auditUser|审核人|integer(int32)||
|&emsp;&emsp;viewCount|浏览次数|integer(int32)||
|&emsp;&emsp;createUser|创建人|integer(int32)||
|&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;updateTime|更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"title": "",
		"content": "",
		"status": 0,
		"type": 0,
		"isTop": 0,
		"publishTime": "",
		"auditTime": "",
		"auditUser": 0,
		"viewCount": 0,
		"createUser": 0,
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>热门公告</strong></summary>



**接口地址**:`/announcements/hot`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|limit||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListAnnouncementVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|array|AnnouncementVO|
|&emsp;&emsp;id|ID|integer(int32)||
|&emsp;&emsp;title|标题|string||
|&emsp;&emsp;content|内容|string||
|&emsp;&emsp;status|状态|integer(int32)||
|&emsp;&emsp;type|类型|integer(int32)||
|&emsp;&emsp;isTop|是否置顶|integer(int32)||
|&emsp;&emsp;publishTime|发布时间|string(date-time)||
|&emsp;&emsp;auditTime|审核时间|string(date-time)||
|&emsp;&emsp;auditUser|审核人|integer(int32)||
|&emsp;&emsp;viewCount|浏览次数|integer(int32)||
|&emsp;&emsp;createUser|创建人|integer(int32)||
|&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;updateTime|更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": [
		{
			"id": 0,
			"title": "",
			"content": "",
			"status": 0,
			"type": 0,
			"isTop": 0,
			"publishTime": "",
			"auditTime": "",
			"auditUser": 0,
			"viewCount": 0,
			"createUser": 0,
			"createTime": "",
			"updateTime": ""
		}
	]
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


## 模块：村务管理-村务事项-公示
<a id="模块-村务管理-村务事项-公示"></a>



<details>
<summary><strong>根据id获取村务事项-公示详情</strong></summary>



**接口地址**:`/cadre/village-affairs/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVillageAffairDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||VillageAffairDetailVO|VillageAffairDetailVO|
|&emsp;&emsp;id|主键|integer(int32)||
|&emsp;&emsp;affairType|事项类型|string||
|&emsp;&emsp;title|标题|string||
|&emsp;&emsp;summary|摘要|string||
|&emsp;&emsp;content|正文（富文本 HTML）|string||
|&emsp;&emsp;amount|金额|number||
|&emsp;&emsp;attachments|附件 URL 列表，JSON 数组字符串|string||
|&emsp;&emsp;status|状态：0草稿 1待审核 2已发布 3已下架|integer(int32)||
|&emsp;&emsp;auditUserId|审核人|integer(int32)||
|&emsp;&emsp;auditTime|审核时间|string(date-time)||
|&emsp;&emsp;auditRemark|审核备注|string||
|&emsp;&emsp;publishTime|发布时间|string(date-time)||
|&emsp;&emsp;viewCount|浏览次数|integer(int32)||
|&emsp;&emsp;createUser|创建人|integer(int32)||
|&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;updateTime|更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"affairType": "",
		"title": "",
		"summary": "",
		"content": "",
		"amount": 0,
		"attachments": "",
		"status": 0,
		"auditUserId": 0,
		"auditTime": "",
		"auditRemark": "",
		"publishTime": "",
		"viewCount": 0,
		"createUser": 0,
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>更新村务事项-公示</strong></summary>



**接口地址**:`/cadre/village-affairs/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "affairType": "",
  "title": "",
  "summary": "",
  "content": "",
  "amount": 0,
  "attachments": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||
|villageAffairUpdateDTO|村务事项/公示更新DTO|body|true|VillageAffairUpdateDTO|VillageAffairUpdateDTO|
|&emsp;&emsp;affairType|事项类型：FINANCE财务 PROJECT项目 POLICY政策 OTHER其他||true|string||
|&emsp;&emsp;title|标题||true|string||
|&emsp;&emsp;summary|摘要||false|string||
|&emsp;&emsp;content|正文（富文本 HTML）||false|string||
|&emsp;&emsp;amount|金额，仅 affairType=FINANCE 使用||false|number||
|&emsp;&emsp;attachments|附件 URL 列表，JSON 数组字符串||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>删除村务事项-公示</strong></summary>



**接口地址**:`/cadre/village-affairs/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>分页查询村务事项-公示列表</strong></summary>



**接口地址**:`/cadre/village-affairs`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|status||query|false|integer(int32)||
|affairType||query|false|string||
|title||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageVillageAffairSimpleVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageVillageAffairSimpleVO|IPageVillageAffairSimpleVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|VillageAffairSimpleVO|
|&emsp;&emsp;&emsp;&emsp;id|主键|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;affairType|事项类型|string||
|&emsp;&emsp;&emsp;&emsp;title|标题|string||
|&emsp;&emsp;&emsp;&emsp;summary|摘要|string||
|&emsp;&emsp;&emsp;&emsp;amount|金额|number||
|&emsp;&emsp;&emsp;&emsp;status|状态：0草稿 1待审核 2已发布 3已下架|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;publishTime|发布时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"affairType": "",
				"title": "",
				"summary": "",
				"amount": 0,
				"status": 0,
				"publishTime": "",
				"createTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>创建村务事项-公示</strong></summary>



**接口地址**:`/cadre/village-affairs`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "affairType": "",
  "title": "",
  "summary": "",
  "content": "",
  "amount": 0,
  "attachments": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|villageAffairCreateDTO|村务事项/公示创建DTO|body|true|VillageAffairCreateDTO|VillageAffairCreateDTO|
|&emsp;&emsp;affairType|事项类型：FINANCE财务 PROJECT项目 POLICY政策 OTHER其他||true|string||
|&emsp;&emsp;title|标题||true|string||
|&emsp;&emsp;summary|摘要||false|string||
|&emsp;&emsp;content|正文（富文本 HTML）||false|string||
|&emsp;&emsp;amount|金额，仅 affairType=FINANCE 使用||false|number||
|&emsp;&emsp;attachments|附件 URL 列表，JSON 数组字符串||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultInteger|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": 0
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>审核村务事项-公示</strong></summary>



**接口地址**:`/cadre/village-affairs/{id}/audit`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "status": 0,
  "auditRemark": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||
|villageAffairAuditDTO|村务事项审核DTO|body|true|VillageAffairAuditDTO|VillageAffairAuditDTO|
|&emsp;&emsp;status|审核结果：2通过发布 1打回待审核（保持待审核但写备注） 3下架||true|integer(int32)||
|&emsp;&emsp;auditRemark|审核备注/驳回原因||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>前台分页查询村务事项-公示列表</strong></summary>



**接口地址**:`/public/village-affairs`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|current||query|false|integer(int64)||
|size||query|false|integer(int64)||
|affairType||query|false|string||
|title||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageVillageAffairSimpleVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||IPageVillageAffairSimpleVO|IPageVillageAffairSimpleVO|
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;records||array|VillageAffairSimpleVO|
|&emsp;&emsp;&emsp;&emsp;id|主键|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;affairType|事项类型|string||
|&emsp;&emsp;&emsp;&emsp;title|标题|string||
|&emsp;&emsp;&emsp;&emsp;summary|摘要|string||
|&emsp;&emsp;&emsp;&emsp;amount|金额|number||
|&emsp;&emsp;&emsp;&emsp;status|状态：0草稿 1待审核 2已发布 3已下架|integer(int32)||
|&emsp;&emsp;&emsp;&emsp;publishTime|发布时间|string(date-time)||
|&emsp;&emsp;&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;size||integer(int64)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"pages": 0,
		"total": 0,
		"current": 0,
		"records": [
			{
				"id": 0,
				"affairType": "",
				"title": "",
				"summary": "",
				"amount": 0,
				"status": 0,
				"publishTime": "",
				"createTime": ""
			}
		],
		"size": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```



</details>


<details>
<summary><strong>根据id获取村务事项-公示详情</strong></summary>



**接口地址**:`/public/village-affairs/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultVillageAffairDetailVO|
|400|Bad Request|ResultObject|
|500|Internal Server Error|ResultObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data||VillageAffairDetailVO|VillageAffairDetailVO|
|&emsp;&emsp;id|主键|integer(int32)||
|&emsp;&emsp;affairType|事项类型|string||
|&emsp;&emsp;title|标题|string||
|&emsp;&emsp;summary|摘要|string||
|&emsp;&emsp;content|正文（富文本 HTML）|string||
|&emsp;&emsp;amount|金额|number||
|&emsp;&emsp;attachments|附件 URL 列表，JSON 数组字符串|string||
|&emsp;&emsp;status|状态：0草稿 1待审核 2已发布 3已下架|integer(int32)||
|&emsp;&emsp;auditUserId|审核人|integer(int32)||
|&emsp;&emsp;auditTime|审核时间|string(date-time)||
|&emsp;&emsp;auditRemark|审核备注|string||
|&emsp;&emsp;publishTime|发布时间|string(date-time)||
|&emsp;&emsp;viewCount|浏览次数|integer(int32)||
|&emsp;&emsp;createUser|创建人|integer(int32)||
|&emsp;&emsp;createTime|创建时间|string(date-time)||
|&emsp;&emsp;updateTime|更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {
		"id": 0,
		"affairType": "",
		"title": "",
		"summary": "",
		"content": "",
		"amount": 0,
		"attachments": "",
		"status": 0,
		"auditUserId": 0,
		"auditTime": "",
		"auditRemark": "",
		"publishTime": "",
		"viewCount": 0,
		"createUser": 0,
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|响应状态码，200表示成功|integer(int32)|integer(int32)|
|message|响应消息|string||
|data|响应数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "success",
	"data": {}
}
```
</details>

