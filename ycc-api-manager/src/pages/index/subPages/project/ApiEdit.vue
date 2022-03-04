<template>
    <div class="api-edit">
        <p >
            <h2 style="font-weight:500;margin-bottom:10px"> 
               {{title}} 
            </h2>
        </p>
        <MyCard title="基础信息">
            <template #extra>
              <div style="padding:6px">
                 <span class="my-tag" @click="toBack()">返回接口列表</span>
                <span class="my-tag my-tag-info" @click="saveData">保存接口</span>
              </div>
          
            </template>
            <Row>
                <Col span="2" class="text-full-right" >
                    <label class="my-label">URL</label>
                </Col>

                <Col span="10">
                    <Input v-model="apiEntity.apiUrl" placeholder="请输入URL"/>
                </Col>

                 <Col span="2" offset="1" class="text-full-right" >
                    <label class="my-label">分组</label>
                </Col>

                <Col span="9">
                     <Select v-model="apiEntity.groupId" filterable>
                        <Option v-for="item in groupItemDatas" :value="item.value" :key="item.value">{{ item.label }}</Option>
                    </Select>
                </Col>
            </Row>

            <Row style="margin-top:15px">
                <Col span="2" class="text-full-right" >
                    <label class="my-label">Method</label>
                </Col>

                <Col span="10">
                    <Select v-model="apiEntity.reqMethod" filterable>
                        <Option v-for="item in $parent.requestMethods" :value="item" :key="item" >{{item}}</Option>
                    </Select>
                </Col>

                 <Col span="2" offset="1" class="text-full-right" >
                    <label class="my-label">状态</label>
                </Col>

                <Col span="9">
                    <RadioGroup v-model="apiEntity.status">
                        <Radio :label="1" border>正常</Radio>
                        <Radio :label="2" border>维护</Radio>
                        <Radio :label="3" border>废弃</Radio>
                    </RadioGroup>
                </Col>
            </Row>

            <Row  style="margin-top:15px">
                <Col span="2" class="text-full-right" >
                    <label class="my-label">名称</label>
                </Col>

                <Col span="22">
                    <Input v-model="apiEntity.apiDesc" placeholder="请输入名称"/>
                </Col>
            </Row>
        </MyCard>

        <MyCard title="请求头信息" style="margin-top:20px">
            <template #extra>
                <span class="tools-add" @click="headers.push({key:'',value:''})">
                     <Icon type="md-add-circle" size="20"/>
                </span>
            </template>
           
           <Row v-for="(item,index) in headers" :key="index" style="margin-top:10px;margin-bottom:10px">
                <Col span="2" class="text-full-right" >
                    <label class="my-label">Key</label>
                </Col>

                <Col span="8">
                  <AutoCompleteWrap
                     v-model="item.key"
                     placeholder="请输入请求头名称"
                    :datas="$parent.requestHeaderNames"
                  ></AutoCompleteWrap>

                </Col>

                 <Col span="2" offset="1" class="text-full-right" >
                    <label class="my-label">Value</label>
                </Col>

                <Col span="8">
                     <Input v-model="item.value" placeholder="请输入VALUE"/>
                </Col>

                <Col span="1">
                    <span class="del-btn" @click="headers.splice(index,1)">
                        <Icon type="md-close" size="18"/>
                    </span>
                </Col>

            </Row>
        </MyCard>

        <MyCard title="请求参数" style="margin-top:20px">
          
            <template  #extra>

                 <template v-if="tabIndex==0">
                     <span class="tools-add" >
                      <Poptip trigger="hover" word-wrap width="550">
                        <Icon type="md-help" size="17" />
                        <template #content>
                            <p>
                              QUERY参数就是把参数拼接到请求的URL中,格式如：<b >http://www.baidu.com/<span style="color:#e6813a">?a=test&b=1</span></b>
                            </p>
                        </template>
                    </Poptip>
                    </span>

                    <span class="tools-add" @click="reqContent.query.definitions.push({name:'',desc:'',type:'String',required:1})">
                        <Icon type="md-add-circle" size="20"/>
                    </span>
                </template>

                 <template v-if="tabIndex==1">
                     <span class="tools-add" >
                      <Poptip trigger="hover" word-wrap width="550">
                        <Icon type="md-help" size="17" />
                        <template #content>
                            <p>
                              URL参数也会把参数拼接到请求的URL中,但是和QUERY不一样，格式如：<b >http://www.baidu.com/<span style="color:#e6813a">param1</span>/<span style="color:#e6813a">param2</span></b>
                              ,URL中需要使用<b >/{paramName}</b>方式进行参数声明,如：<span style="color:#e6813a">/user/{userId}</span>
                            </p>
                        </template>
                    </Poptip>
                    </span>

                </template>

                <template v-if="tabIndex==2">

                    <Poptip  placement="bottom" word-wrap width="550">
                      <span class="example-add-btn my-tag my-tag-info">
                          添加示例数据
                      </span>

                      <template #content>
                         <div style="padding:8px 2px">
                             <div class="generate-json-definite ">
                                <span class="generate" :class="{'generate-info':bodyExampleJsoned}" @click="()=>{
                                  reqContent.body.example=JSON.stringify(JSON.parse(reqContent.body.example),'',4)
                                    reqContent.body.definitions=generateJson(JSON.parse(reqContent.body.example))
                                  }">生成参数信息</span>
                             </div>
                             <div class="json-content" style="border-color:#3C8DBC"  >
                                <textarea class="json-textarea" v-model="reqContent.body.example" placeholder='请填写JSON格式示例数据，如:{"username":"zhangsan","password":"123456"}'></textarea>
                            </div>
                         </div>
                      </template>
                  </Poptip>

                    <span class="tools-add" >
                      <Poptip trigger="hover" word-wrap width="550">
                        <Icon type="md-help" size="17" />
                        <template #content>
                            <p>
                              Body参数会把参数放在请求的请求Body中,默认情况下只有<span style="color:#e6813a">POST</span>和<span style="color:#e6813a">PUT</span>才会使用请求体参数
                            </p>
                        </template>
                    </Poptip>
                    </span>

                    <span class="tools-add" @click="reqContent.body.definitions.push({name:'',desc:'',type:'String',required:1,propertys:[]})">
                        <Icon type="md-add-circle" size="20"/>
                    </span>
                </template>
               
            </template>
            <Tabs v-model="tabIndex">
              <TabPane label="QUERY参数">
                  <Row v-for="(item,index) in reqContent.query.definitions" :key="index" style="margin-top:10px;margin-bottom:10px">
                      <Col span="2" class="text-full-right" >
                          <label class="my-label">参数</label>
                      </Col>

                      <Col span="4">
                        <Input v-model="item.name" placeholder="请输入参数名"/>

                      </Col>

                      <Col span="2"  class="text-full-right" >
                          <label class="my-label">说明</label>
                      </Col>

                      <Col span="4">
                          <Input v-model="item.desc" placeholder="请输入描述信息"/>
                      </Col>

                      <Col span="2"  class="text-full-right" >
                          <label class="my-label">类型</label>
                      </Col>

                      <Col span="4">
                          <Select v-model="item.type" disabled>
                              <Option value="String">String</Option>
                          </Select>
                      </Col>

                      <Col span="3" offset="1">
                          <Select v-model="item.required" v-if="item.type!=='Object'">
                              <Option :value="1" >必填</Option>
                              <Option :value="0" >非必填</Option>
                          </Select>
                      </Col>

                      <Col span="1">
                          <span class="del-btn" @click="reqContent.query.definitions.splice(index,1)">
                              <Icon type="md-close" size="18"/>
                          </span>
                      </Col>

                  </Row>
                         
                    
              </TabPane>


              <TabPane label="URL参数" :disabled="urlParamDisable">
                   <Row v-for="(item,index) in reqContent.param.definitions" :key="index" style="margin-top:10px;margin-bottom:10px">
                      <Col span="2" class="text-full-right" >
                          <label class="my-label">参数</label>
                      </Col>

                      <Col span="4">
                        <Input v-model="item.name" placeholder="请输入参数名" disabled/>

                      </Col>

                      <Col span="2"  class="text-full-right" >
                          <label class="my-label">说明</label>
                      </Col>

                      <Col span="4">
                          <Input v-model="item.desc" placeholder="请输入描述信息"/>
                      </Col>

                      <Col span="2"  class="text-full-right" >
                          <label class="my-label">类型</label>
                      </Col>

                      <Col span="4">
                          <Select v-model="item.type"  disabled>
                              <Option value="Object"  >Object</Option>
                              <Option v-for="item in dataTypes" :value="item.typeName" :key="item.id" >{{item.typeName}}</Option>
                          </Select>
                      </Col>

                      <Col span="3" offset="1">
                          <Select v-model="item.required" v-if="item.type!=='Object'" disabled>
                              <Option :value="1" >必填</Option>
                              <Option :value="0" >非必填</Option>
                          </Select>
                      </Col>

                  </Row>
              </TabPane>


              <TabPane label="Body参数" :disabled="apiEntity.reqMethod!=='POST'&&apiEntity.reqMethod!=='PUT'">
                <MyBodyParams
                  :datas="reqContent.body.definitions"
                  :dataTypes="dataTypes"
                />
                   
              </TabPane>
           </Tabs>
        </MyCard>

        <MyCard title="响应结果" style="margin-top:20px" :top-color="false">
          
            <template #extra>
                <div class="res_tools_groups">
                  <span class="top-line"></span>
                  <div :class="{active:responOptionActive==1}" @click="viewType=1;responOptionActive=1">成功的响应</div>
                  <div  :class="{active:responOptionActive==2}" @click="viewType=1;responOptionActive=2">失败的响应</div>
                </div>
            </template>
            <p style="margin-bottom:15px;position: relative;">
                <label style="margin-right:15px">响应数据类型(MIME)</label>
                 <RadioGroup v-model="apiEntity.resDataType">
                    <Radio label="JSON" ></Radio>
                    <Radio label="XML" disabled></Radio>
                    <Radio label="HTML" disabled></Radio>
                    <Radio label="TEXT" disabled></Radio>
                    <Radio label="JSONP" disabled></Radio>
                </RadioGroup>

                <span class="property-add-btn" style="right:280px" v-if="responOptionActive==1&&resDataBox.successed.desced&&viewType==2" @click="generateSuccessPropertyJson()" ><Icon type="md-add-circle" size="16"/>生成属性</span>
                <span class="property-add-btn" style="right:280px" v-if="responOptionActive==2&&resDataBox.errored.desced&&viewType==2" @click="()=>{resDataBox.errored.propertyDescs=generateJson(JSON.parse(resDataBox.errored.json))}"><Icon type="md-add-circle" size="16"/>生成属性</span>

                <span class="property-add-btn"  v-if="responOptionActive==1&&resDataBox.successed.desced" @click="resDataBox.successed.propertyDescs.push({name:'',desc:'',type:'String',required:1,propertys:[]})" ><Icon type="md-add-circle" size="16"/>添加属性</span>
                <span class="property-add-btn"  v-if="responOptionActive==2&&resDataBox.errored.desced" @click="resDataBox.errored.propertyDescs.push({name:'',desc:'',type:'String',required:1,propertys:[]})"><Icon type="md-add-circle" size="16"/>添加属性</span>

                <Checkbox style="position: absolute;right:100px" v-model="resDataBox.successed.desced" v-if="responOptionActive==1" :true-value="true" :false-value="false">属性描述</Checkbox>
                <Checkbox style="position: absolute;right:100px" v-model="resDataBox.errored.desced" v-if="responOptionActive==2" :true-value="true" :false-value="false">属性描述</Checkbox>
                <span class="json-format-btn" v-if="viewType==1" @click="switchView">
                        JSON视图
                </span>

                 <span class="json-format-btn"  v-if="viewType==2" @click="viewType=1">
                       编辑视图
                </span>
            </p>

            <template v-if="responOptionActive==1">
              <div class="json-content" :style="{'border-color':(flag1?'#3C8DBC':'#e7e7e7')}"  >
                  <textarea class="json-textarea" v-if="viewType==1" v-model="resDataBox.successed.json"  @focus="flag1=true" @blur="flag1=false"></textarea>

                  <JsonView :data="jsonViewData" :font-size="13" v-if="viewType==2"></JsonView>
              </div>

              <template v-if="resDataBox.successed.desced">
                   <Divider orientation="left">属性说明</Divider>
                    <MyBodyParams
                      :datas="resDataBox.successed.propertyDescs"
                      :dataTypes="dataTypes"
                    />
              </template>
             
            </template>


            <template v-if="responOptionActive==2"> 
                 <div class="json-content" :style="{'border-color':(flag1?'#3C8DBC':'#e7e7e7')}"  >
                    <textarea class="json-textarea" v-if="viewType==1" v-model="resDataBox.errored.json"  @focus="flag1=true" @blur="flag1=false"></textarea>

                      <JsonView :data="jsonViewData" :font-size="13" v-if="viewType==2"></JsonView>
                  </div>

               <template v-if="resDataBox.errored.desced">
                   <Divider orientation="left">属性说明</Divider>
                    <MyBodyParams
                      :datas="resDataBox.errored.propertyDescs"
                      :dataTypes="dataTypes"
                    />
              </template>
            </template>
           
        </MyCard>

    <MyLoading :show="loading" type="3" text="数据准备中..."></MyLoading>

    </div>
</template>

<script>
import MyLoading from "@/components/MyLoading";
import MyBodyParams from "@/components/MyBodyParams";
import JsonView from "vue-json-views";
import MyCard from "@/components/MyCard";
import AutoCompleteWrap from "@/components/AutoCompleteWrap";
export default {
  name: "api-edit",
  components: {
    MyCard,
    JsonView,
    AutoCompleteWrap,
    MyBodyParams,
    MyLoading,
  },
  data() {
    return {
      loading: false,
      responOptionActive: 1,
      title: "",
      dataTypes: [],
      headers: [],
      viewType: 1,
      apiEntity: {
        resContent: "",
      },
      resDataBox: {
        successed: {
          json: "",
          desced: false,
          propertyDescs: [],
        },
        errored: {
          json: "",
          desced: false,
          propertyDescs: [],
        },
      },
      jsonViewData: {},
      requestHeaderNames: [],
      reqContent: {
        body: {
          example: "",
          definitions: [],
        },
        query: {
          definitions: [],
        },
        param: {
          definitions: [],
        },
      },
      flag1: false,
      tabIndex: 0,
    };
  },
  created() {
    if (!this.$route.query.apiInfoId || this.$route.query.apiInfoId == "") {
      this.title = "新增接口";
      this.initDatas();
    } else {
      this.title = "编辑接口";

      this.loadApiInfo(this.$route.query.apiInfoId, () => {
        // 回显请求头
        this.headers = this.apiEntity.reqHeaders
          ? JSON.parse(this.apiEntity.reqHeaders)
          : this.headers;
        // 回显请求参数
        this.reqContent = this.apiEntity.reqContent
          ? JSON.parse(this.apiEntity.reqContent)
          : this.apiEntity.reqContent;
        // 回显相应内容
        this.resDataBox = this.apiEntity.resContent
          ? JSON.parse(this.apiEntity.resContent)
          : this.apiEntity.resContent;

        setTimeout(() => {
          // 回显响应体说明
          this.resDataBox.successed.propertyDescs = JSON.parse(
            this.apiEntity.resContent
          ).successed.propertyDescs;
          this.resDataBox.errored.propertyDescs = JSON.parse(
            this.apiEntity.resContent
          ).errored.propertyDescs;

          // 回显url参数
          this.reqContent.param.definitions = JSON.parse(
            this.apiEntity.reqContent
          ).param.definitions;
        }, 10);
      });
    }
    this.loadDataTypes();
  },
  mounted() {
    document.querySelector(".res_tools_groups > .top-line").style.width =
      document.querySelector(".res_tools_groups .active").offsetWidth + "px";
  },
  watch: {
    "apiEntity.apiUrl": function (newVal, oldVal) {
      const newArray = this.urlParamList.map((name) => {
        return {
          name: name,
          desc: "",
          type: "String",
          required: 1,
        };
      });

      this.reqContent.param.definitions = newArray;
    },
    responOptionActive: function (newVal, oldVal) {
      var _el = document.querySelector(".res_tools_groups > .top-line");
      setTimeout(() => {
        _el.style.left =
          document.querySelector(".res_tools_groups .active").offsetLeft + "px";
      }, 1);
    },
  },
  computed: {
    groupItemDatas() {
      return this.$bus.storage.session.getItem("groupItemDatas");
    },
    urlParamDisable() {
      var url = this.apiEntity.apiUrl;
      if (!url) {
        return true;
      }
      var start = url.indexOf("/{");
      var end = url.indexOf("}");
      return !(start != -1 && end != -1 && end - start > 2);
    },
    urlParamList() {
      const newArray = [];
      if (this.urlParamDisable) {
        return newArray;
      }

      var url = this.apiEntity.apiUrl;

      var offset = 0;

      while (true) {
        var start = url.indexOf("/{", offset);
        var end = url.indexOf("}", start);

        if (start == -1 || end == -1) {
          break;
        }

        if (end - start > 2) {
          newArray.push(url.substring(start + 2, end));
        }
        offset = end + 1;
      }

      return newArray;
    },
    reqDataType() {
      if (this.headers && this.headers.length > 0) {
        for (let item of this.headers) {
          if (
            item.key === "Content-Type" ||
            item.key === "content-type" ||
            (item.key === "contentType" && item.value !== "")
          ) {
            return item.value;
          }
        }
      }
      return "application/json";
    },
    bodyExampleJsoned() {
      try {
        let json = JSON.parse(this.reqContent.body.example);
        return Object.getOwnPropertyNames(json).length > 0;
      } catch (error) {
        return false;
      }
    },
  },
  methods: {
    generateSuccessPropertyJson() {
       this.resDataBox.successed.propertyDescs=this.generateJson(JSON.parse(this.resDataBox.successed.json))
    },
    //保存数据
    saveData() {
      if (this.headers && this.headers.length > 0) {
        this.apiEntity.reqHeaders = JSON.stringify(this.headers);
      }else{
         this.apiEntity.reqHeaders = '[]';
      }
      if (
        this.reqContent.body.example &&
        this.reqContent.body.example.trim().length > 0
      ) {
        try {
          JSON.parse(this.reqContent.body.example);
        } catch (error) {
          this.$Message.warning("body参数中添加的示例数据不是标准JSON");
          return;
        }
      }
      this.apiEntity.reqDataType = this.reqDataType;
      this.apiEntity.reqContent = JSON.stringify(this.reqContent);
      this.apiEntity.resContent = JSON.stringify(this.resDataBox);
      this.apiEntity.projectVersionId = this.$route.query.proVersionId;

      this.$server.api.updateApiInfo(this.apiEntity).then((res) => {
        var { data } = res;
        if (data.code == 200) {
          this.$Notice.success({
            title: "更新接口",
            desc: "更新接口成功",
          });
          this.$bus.$emit("api-list::initApiList");
        } else {
          this.$Message.warning(data.msg);
        }
      });
    },
    toBack() {
      history.back();
    },
    loadApiInfo(apiInfoId, callback) {
      this.loading = true;
      this.$server.api.getById(apiInfoId).then((res) => {
        var { data } = res;
        this.loading = false;
        if (data.code == 200) {
          this.apiEntity = data.data;
          if (callback) {
            callback();
          }
        } else {
          this.$Message.warning(data.msg);
        }
      });
    },
    loadDataTypes() {
      this.$server.dataType.getDataTypeList().then((res) => {
        const { data } = res;

        if (data.code == 200) {
          this.dataTypes = data.data;
        } else {
          this.$Message.warning(data.msg);
        }
      });
    },
    initDatas() {
      this.apiEntity = {
        reqMethod: "GET",
        groupId: "0",
        status: 1,
        resDataType: "JSON",
      };
    },
    jsonFormat(json, callback, showError = true) {
      if (!json || json.length == 0) {
        return;
      }

      try {
        var _json = JSON.parse(json);

        if (callback) {
          callback(_json);
        }
      } catch (error) {
        if (showError) {
          this.$Message.warning("JSON格式不正确");
        }
        return;
      }
    },
    switchView() {
      var json =
        this.responOptionActive == 1
          ? this.resDataBox.successed.json
          : this.resDataBox.errored.json;
      this.jsonFormat(json, (res) => {
        this.jsonViewData = res;

        if (this.responOptionActive == 1) {
          this.resDataBox.successed.json = JSON.stringify(res, "", 4);
        } else {
          this.resDataBox.errored.json = JSON.stringify(res, "", 4);
        }
        this.viewType = 2;
      });
    },
    generateJson(json) {
      let newArray = [];
      if (!json) {
        return newArray;
      }

      if (
        typeof json == "object" &&
        Object.getOwnPropertyNames(json).length == 0
      ) {
        return newArray;
      }

   
      for (let key in json) {
        const obj = {
          name: key,
          desc: "",
          required: 1,
          propertys: [],
        };

        let prop = json[key];
        if (!prop) {
          obj.type = "String";
        } else if ( typeof prop === "object" && prop.constructor.name === "Object") {
          obj.type = "Object";
          obj.propertys = this.generateJson(prop);
        }  else if (typeof prop === "object" && prop.constructor.name === "Array") {
             obj.type = "Array";
            if (prop.length != 0) {
              var item0 = prop[0];
              if(item0&&typeof item0=='object'&&item0.constructor.name==='Object'){
                 obj.type ="Object";
                 obj.propertys = this.generateJson(item0);
                 obj.desc="数组对象嵌套 - [{}]"
              }else{
                obj.name = obj.name + " - " + item0.constructor.name+"[]"
              }
            } 
         
        }else {
          obj.type = prop.constructor.name;
        }

        newArray.push(obj);
      }
      return newArray;
    },
  },
};
</script>

<style lang="scss" scoped>
.api-edit {
  overflow-y: auto;
  padding: 12px;
  width: 100%;
  height: calc(100vh - 50px);
  background-color: #ecf0f5;

  .my-label {
    position: relative;
    top: 5px;
    right: 15px;
    font-family: "Source Sans Pro", "Helvetica Neue", Helvetica, Arial,
      sans-serif;
    font-weight: 700;
  }

  .del-btn {
    display: inline-block;
    padding: 2px;
    border-radius: 50%;
    width: 22px;
    height: 22px;
    cursor: pointer;
    position: relative;
    left: 20px;
    top: 5px;
    &:hover {
      background-color: rgb(73, 114, 160);
      color: #fff;
    }
  }
  .tools-add {
    display: inline-block;
    padding: 2px;
    width: 24px;
    height: 24px;
    cursor: pointer;
    color: silver;
    font-weight: bold;
    position: relative;
    right: 5px;
    top: 7px;
    &:hover {
      color: rgb(73, 114, 160);
    }
  }

  .json-format-btn {
    display: inline-block;
    float: right;
    margin-right: 15px;
    font-size: 12px;
    color: tomato;
    cursor: pointer;
    position: relative;
    padding: 2px;
    border: 1px solid transparent;
    &:hover {
      border-color: #f4f4f4;
      background-color: #f4f4f4;
    }

    user-select: none;
  }

  .json-content {
    min-height: 120px;
    width: 100%;
    border: 1px solid #e7e7e7;
    position: relative;

    .json-textarea {
      width: 100%;
      min-height: 120px;
      height: auto;
      border: none;
      outline: none;
      padding: 5px;
      font-size: 12px;
    }
  }
}

.ivu-tabs {
  overflow: visible !important;
}

.res_tools_groups {
  height: 100%;
  min-width: 200px;
  font-size: 12px;
  position: relative;
  display: flex;
  text-shadow: 0 0 2px rgba($color: #000000, $alpha: 0.2);
  flex-direction: row;

  .top-line {
    position: absolute;
    display: inline-block;
    top: 0;
    width: 100px;
    height: 3px;
    border-radius: 3px;
    background-color: rgb(53, 151, 216);
    left: 0;
    transition: left 0.2s ease;
  }

  & > div {
    border-top: 3px solid transparent;
    text-align: center;
    line-height: 35px;
    flex: 1;
    border-left: 1px solid transparent;
    border-right: 1px solid transparent;
    border-bottom: 2px solid transparent;
    cursor: pointer;
    &:not(.active):not(.top-line):hover {
      background-color: #f4f4f4;
    }

    &.active {
      //  border-top: 3px solid #367FA9;
      border-left: 1px solid #f4f4f4;
      border-right: 1px solid #f4f4f4;
      border-bottom: 2px solid #fff;
    }
  }
}

.property-add-btn {
  position: absolute;
  display: inline-block;
  z-index: 999;
  right: 200px;
  text-align: center;
  height: 20px;
  background-color: #fff;
  font-size: 13px;
  user-select: none;
  cursor: pointer;

  &:hover {
    background-color: aquamarine;
  }
}

.example-add-btn {
  position: relative;
  top: 6px;
  margin-right: 15px;
}

.generate-json-definite {
  padding: 6px 8px;
  .generate {
    padding: 2px 6px;
    border-width: 1px;
    border-style: solid;
    border-radius: 3px;
    cursor: pointer;
    border-color: rgb(189, 196, 214);
    color: rgb(189, 196, 214);
    transition: all 0.3s;

    &.generate-info {
      border-color: rgb(10, 109, 175);
      color: rgb(10, 109, 175);
    }
  }
}
</style>