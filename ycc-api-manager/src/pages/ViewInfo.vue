<template>
  <div class="view-info">
    <header class="header">
      <div class="content">
        <div class="title">
          <svg
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.0"
            xmlns="http://www.w3.org/2000/svg"
            p-id="4123"
            width="20"
            height="20"
          >
            <path
              d="M213.333333 128h85.333334v85.333333H213.333333v213.333334a85.333333 85.333333 0 0 1-85.333333 85.333333 85.333333 85.333333 0 0 1 85.333333 85.333333v213.333334h85.333334v85.333333H213.333333c-45.653333-11.52-85.333333-38.4-85.333333-85.333333v-170.666667a85.333333 85.333333 0 0 0-85.333333-85.333333H0v-85.333334h42.666667a85.333333 85.333333 0 0 0 85.333333-85.333333V213.333333a85.333333 85.333333 0 0 1 85.333333-85.333333m597.333334 0a85.333333 85.333333 0 0 1 85.333333 85.333333v170.666667a85.333333 85.333333 0 0 0 85.333333 85.333333h42.666667v85.333334h-42.666667a85.333333 85.333333 0 0 0-85.333333 85.333333v170.666667a85.333333 85.333333 0 0 1-85.333333 85.333333h-85.333334v-85.333333h85.333334v-213.333334a85.333333 85.333333 0 0 1 85.333333-85.333333 85.333333 85.333333 0 0 1-85.333333-85.333333V213.333333h-85.333334V128h85.333334m-298.666667 512a42.666667 42.666667 0 0 1 42.666667 42.666667 42.666667 42.666667 0 0 1-42.666667 42.666666 42.666667 42.666667 0 0 1-42.666667-42.666666 42.666667 42.666667 0 0 1 42.666667-42.666667m-170.666667 0a42.666667 42.666667 0 0 1 42.666667 42.666667 42.666667 42.666667 0 0 1-42.666667 42.666666 42.666667 42.666667 0 0 1-42.666666-42.666666 42.666667 42.666667 0 0 1 42.666666-42.666667m341.333334 0a42.666667 42.666667 0 0 1 42.666666 42.666667 42.666667 42.666667 0 0 1-42.666666 42.666666 42.666667 42.666667 0 0 1-42.666667-42.666666 42.666667 42.666667 0 0 1 42.666667-42.666667z"
              fill="#f4ea2a"
              p-id="4124"
            ></path>
          </svg>
          <span>{{ viewEntity.viewName }}</span>
        </div>

        <div class="export-btn-box">
          <button @click="exportClick">Export</button>
        </div>
        <div class="versions">
          <select v-model="versionActive">
            <option
              v-for="(item, index) of viewEntity.versionList"
              :key="index"
              :value="item.versionId"
            >
              {{ item.versionName }}
            </option>
          </select>
        </div>
      </div>
    </header>

    <div class="container">
      <div class="desc-box">
        <div style="font-size: 28px; font-weight: 700; color: #000">
          {{ viewEntity.projectName }}
        </div>
        <div class="desc">
          <p>
            <div style="max-width:300px;word-break: break-all;">{{ projectEntity.projectDesc }}</div>
          </p>
          <br />
        </div>

        <div class="content">
          <div>
            <label class="title-item">项目环境</label>
            <ul style="list-style-type: none">
              <li style="padding: 2px">
                <label>开发环境地址 : </label>

                <span class="my-link">
                  {{ projectEntity.profileDevUrl }}
                </span>
              </li>
              <li style="padding: 2px">
                <label>测试环境地址 : </label>
                <span class="my-link">
                  {{ projectEntity.profileTestUrl }}
                </span>
              </li>
            </ul>
          </div>

          <div style="padding-top: 28px">
            <Input size="large" placeholder="接口名称/接口URL" v-model="apiQueryEntity.likeBox.likeValue" @keydown.enter.native="loadApiInfo">
              <Select
                v-model="groupActive"
                slot="prepend"
                style="min-width: 120px"
              >
                {{ groupsArray }}
                <Option
                  v-for="(item, index) in groupsArray"
                  :key="index"
                  :value="item.id"
                  >{{ item.groupName }}</Option
                >
              </Select>
              <Button slot="append" @click="()=>{apiQueryEntity.pageNum=1;loadApiInfo();}">搜索</Button>
            </Input>
          </div>
        </div>
      </div>
      <div class="main-container">
          <ul class="api-items">
              <li class="api-item" v-for="(item,index) in apiListData" :key="index">
                <div class="item-info-simple-box">
                   <div class="title-box">
                        <span class="title-first" :title="item.apiUrl" @click="openAndCloseBox({row:item,index:index})">
                          {{item.apiUrl}}
                        </span>
                         <span class="title-last" :title="item.apiDesc">
                             :{{item.apiDesc}}{{item._open}}
                         </span>
                   </div>
                   <div class="options-box">
                        <span class="option-item">
                          <span class="my-tag my-tag-warning" v-if="item.reqMethod=='POST'"> {{item.reqMethod}}</span>
                          <span class="my-tag my-tag-error" v-if="item.reqMethod=='DELETE'"> {{item.reqMethod}}</span>
                          <span class="my-tag my-tag-info" v-if="item.reqMethod=='GET'"> {{item.reqMethod}}</span>
                          <span class="my-tag my-tag-alter" v-if="item.reqMethod=='PUT'"> {{item.reqMethod}}</span>
                          <span class="my-tag" v-if="item.reqMethod=='HEAD'||item.reqMethod=='OPTIONS'||item.reqMethod=='PATCH'"> {{item.reqMethod}}</span>
                        </span>
                        <span class="option-item" style="color:#000000">
                            {{groupNameFilter(item.groupId) }}
                        </span>
                         <span class="option-item" v-if="viewEntity.isTestable===1">
                            <a href="javaScript:">
                              <span  title="调试" style="position: relative;
                                  top: 3px;">
                                <svg t="1611062215045" class="icon" viewBox="0 0 1056 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5088" width="22" height="22"><path d="M266.0317958 231.07929925l91.30758782-24.03438189 67.27320676 67.27320593-24.03630423 91.30758865-91.30758866 24.0343819-67.27320593-67.27320593 24.03630424-91.30758866z" fill="#40BF4F" p-id="5089"></path><path d="M301.36743111 220.20664858l55.58371299-14.18038137 69.73718664 70.96917701-24.03438273 88.84360793-58.04961519 12.94839103-67.27320594-67.27320593 24.03630423-91.30758867z" fill="#359941" p-id="5090"></path><path d="M593.13003014 428.87236655l-127.79064652 127.79064568 55.76053546 55.7624578 127.79064653-127.79064651-55.76053547-55.76245697z" fill="#A6A6A6" p-id="5091"></path><path d="M970.2824363 777.15084021L826.22413652 921.20913999 620.66241909 715.64742337l-56.06036417 56.06228649-349.94867597-93.76964703L120.88373191 327.99138687l62.85649192-62.85457041L405.59842457 486.99501721l97.17155475-97.17155474 0.68038139 0.67845986L727.29973399 166.65254822l93.48134985 348.87621039-56.06036416 56.06036416 205.56171661 205.56171744z" fill="#BFBFBF" p-id="5092"></path><path d="M408.31610708 483.41244318L726.70776344 166.65254822l10.31527634 29.07381884-328.7069327 330.68080839L184.07464876 302.16379562l-29.64464703 29.64464704 86.72366217 320.99017648 320.73455237 86.97928546 65.23590563-65.23590564 225.59645489 220.18223227-26.49644027 26.48490876-205.54826357-205.43678806-51.31306906 54.7168991-352.45301826-95.94725207L121.13551137 326.76131803 184.07464876 265.7364747l224.24145832 217.67596848z" fill="#999999" p-id="5093"></path><path d="M700.21708934 185.5802247l-181.13717285 181.13909518L314.20819126 161.84759384l62.85649192-62.85456958 323.15240616 86.58720044z" fill="#999999" p-id="5094"></path><path d="M344.0738662 202.58591943l-35.30103992-35.30103908-1.66635763-2.49473228-0.5862052-2.94255423 0.5862052-2.9406319 1.66635763-2.49473226 62.85457041-62.85649191 2.18721505-1.53181989 2.57929926-0.6899914 2.66002322 0.23256025L704.35127153 178.73028152l17.5130983-17.5150198 2.68116477-1.74131544 3.15781594-0.49971505 3.08670282 0.82837382 2.48512226 2.01039339 1.45109591 2.84837724 93.48134986 348.87813189c0.15568012 1.77206699 0.67653753 3.61909177-1.98925104 7.4246152l-50.625 50.625L981.15508696 777.15084021l-10.87265067 10.87265066-210.9970816-210.9970816-1.66635763-2.49473227-0.58620438-2.94255422 0.58620438-2.94255424 1.66635763-2.49280994 52.92176825-52.92176824L723.32123191 181.50370178l-214.43550705 214.43550706-3.15781594 1.79705237-94.69219784 94.69412016-2.49473227 1.66635847-2.94255424 0.58620438-2.94255422-0.58620438-2.49281077-1.66635847L183.74022383 276.00754562 129.45961434 330.29007663l91.47095727 341.37279355 341.37279273 91.47095645 52.92176824-52.92176825 2.49473227-1.66635764 2.94255423-0.58620437 2.94255424 0.58620437 2.49281075 1.66635764L837.09678719 921.20913999l-10.87265067 10.87265066-205.56171743-205.56171662-50.625 50.625c-1.45878442 1.02057248-2.7964833 2.39478909-7.42653754 1.98925105L212.66220559 685.36660037c-1.61254303-0.75149451-3.47109934-1.22430267-5.43536418-5.43536499L113.45911671 329.98063791c-0.15568012-1.77206699-0.67653753-3.61909177 1.98925105-7.42461603L178.30485967 259.69952998l2.49280993-1.66635765 2.94255423-0.58428286 2.94255423 0.58428286 2.49281076 1.66635764 48.58385504 48.58385587L258.59564824 229.1227221c0.82260847-2.60620697 1.82588242-4.32638086 5.47764807-5.47764806l80.00056989-21.05915461zM253.03343318 614.72223828l-3.70365876 1.03018167-64.90340303-233.38240274 3.70173724-1.03018249 64.90532455 233.38240356z m291.26480631-12.6735477l-13.31741217 7.687927-50.93059488-88.21704199 13.31548983-7.68792701 50.93251722 88.217042z m21.13987774-21.13987775l-13.31548983 7.68792701-50.93251722-88.2189635 13.31548985-7.687927 50.9325172 88.21896349z m21.13987692-21.14180008l-13.31548901 7.68792783-50.93251721-88.217042 13.31548984-7.687927 50.93251638 88.21704118z m21.14180009-21.13987692l-13.31741218 7.687927-50.93251721-88.21704198 13.31741217-7.68792701 50.93251722 88.217042z m21.13987774-21.13987775l-13.31548983 7.68792701-50.93251722-88.21896351 13.31548984-7.68792699 50.93251721 88.21896349z m-199.79000601-229.90553805l-21.05723227 80.00056906c-0.82260847 2.60620697-1.82588242 4.32638086-5.47764807 5.47764808l-79.16066373 20.8381267 82.22430267 82.22430268 91.73618977-91.73619059 3.15781677-1.79897469 11.79135763-11.79135847-83.21412277-83.21412277zM272.32436382 237.37186811l-21.77028754 82.70287536 61.02868716 61.02676566 82.70095469-21.77028755 21.77028753-82.70095385-61.02868799-61.02676565-82.70095385 21.76836603z m52.75647812-75.52427427l198.0755983 198.0755983 168.64044681-168.6385253L379.36145144 107.56698434l-54.2806095 54.2806095z m338.37258004 39.7485055l-0.99558709 3.71134643L380.13985366 129.56406595l0.9975086-3.71134644 282.31605972 75.74337983z" fill="#262626" p-id="5095"></path></svg>
                              </span>
                              </a>
                        </span>
                   </div>
                </div>
                
                
                    <div class="item-info-extra-box"  v-show="item.closed">
                        <p v-if="
                        !(getHeaders(item)&&getHeaders(item).length>0)&&
                        !(  (getRequestParamObj(item).param.definitions&&getRequestParamObj(item).param.definitions.length>0)
                          ||(getRequestParamObj(item).query.definitions&&getRequestParamObj(item).query.definitions.length>0)
                          ||(getRequestParamObj(item).body.definitions&&getRequestParamObj(item).body.definitions.length>0))&&
                          !( (isNotEmpty(getResponseObj(item))&&(isNotEmpty(getResponseObj(item).successed,'json')||isNotEmpty(getResponseObj(item).successed,'propertyDescs')))
                           ||(isNotEmpty(getResponseObj(item))&&(isNotEmpty(getResponseObj(item).errored,'json')||isNotEmpty(getResponseObj(item).errored,'propertyDescs')) ))

                        ">
                          该接口未设置任务请求信息
                        </p>
                        <div class="extra-box-item" v-if="getHeaders(item)&&getHeaders(item).length>0">
                            <span class="full-title" >
                              请求头
                              </span>
                             <Table border :disabled-hover="true" :columns="iviewTableHeadersColumns" row-key="key" :data="getHeaders(item)"></Table>
                        </div>
                        <div class="extra-box-item" v-if="
                          (getRequestParamObj(item).param.definitions&&getRequestParamObj(item).param.definitions.length>0)
                          ||(getRequestParamObj(item).query.definitions&&getRequestParamObj(item).query.definitions.length>0)
                          ||(getRequestParamObj(item).body.definitions&&getRequestParamObj(item).body.definitions.length>0)
                        ">
                            <span class="full-title" >
                              请求参数
                            </span>

                             <div style="margin-bottom:30px" v-if="getRequestParamObj(item).param.definitions&&getRequestParamObj(item).param.definitions.length>0">
                                <p class="chil-title"><span style="border-color:#FF00FF"></span><b>URL参数</b></p>
                                <Table border :disabled-hover="true" row-key="name"   :columns="iviewTableParamsColumns" :data="getRequestParamObj(item).param.definitions"></Table>
                            </div>

                            <div style="margin-bottom:30px" v-if="getRequestParamObj(item).query.definitions&&getRequestParamObj(item).query.definitions.length>0">
                                <p class="chil-title"><span style="border-color:green"></span><b>QUERY参数</b></p>
                                <Table border :disabled-hover="true" row-key="name"   :columns="iviewTableParamsColumns" :data="getRequestParamObj(item).query.definitions"></Table>
                            </div>

                            <div v-if="getRequestParamObj(item).body.definitions&&getRequestParamObj(item).body.definitions.length>0">
                                <p class="chil-title">
                                  <span ></span><b>BODY参数</b> 
                                   <Poptip placement="right" trigger="hover" width="500" v-if="generateExampleData(getRequestParamObj(item).body)!=null">
                                      <a href="javaScript:" class="example-data-link my-tag my-tag-warning"  >
                                        Example
                                        </a>
                                      <div class="api" slot="content">
                                        <JsonView :data="generateExampleData(getRequestParamObj(item).body)" :font-size="13" ></JsonView>
                                      </div>
                                   </Poptip>
                                 
                                </p>
                                <Table border :disabled-hover="true" row-key="_id"  :columns="iviewTableParamsColumns" :data="formatTreeTableData(getRequestParamObj(item).body.definitions,'propertys')"></Table>
                            </div>
                           

                        </div>
                         <div class="extra-box-item" v-if="
                           (isNotEmpty(getResponseObj(item))&&(isNotEmpty(getResponseObj(item).successed,'json')||isNotEmpty(getResponseObj(item).successed,'propertyDescs')))
                           ||(isNotEmpty(getResponseObj(item))&&(isNotEmpty(getResponseObj(item).errored,'json')||isNotEmpty(getResponseObj(item).errored,'propertyDescs')) )
                         ">

                            <span class="full-title" >
                              响应数据
                            </span>

                            <div style="margin-bottom:20px" class="res-item-box color-green" v-if="isNotEmpty(getResponseObj(item))&&(isNotEmpty(getResponseObj(item).successed,'json')||isNotEmpty(getResponseObj(item).successed,'propertyDescs'))">
                              <div class="res-item-title">
                                  成功的响应
                              </div>
                              <div class="jsonView-container">
                                  <JsonView v-if="formatJSon(getResponseObj(item).successed.json)!=null" :data="formatJSon(getResponseObj(item).successed.json)" :font-size="13" ></JsonView>
                              </div>
                              <template v-if="getResponseObj(item).successed.desced">
                                  <Divider orientation="left">属性说明</Divider>
                                  <Table border :disabled-hover="true" row-key="_id" :columns="iviewTableResponseColumns" :data="formatTreeTableData(getResponseObj(item).successed.propertyDescs,'propertys')"></Table>
                              </template>
                              
                            </div>


                             <div  class="res-item-box color-error" v-if="isNotEmpty(getResponseObj(item))&&(isNotEmpty(getResponseObj(item).errored,'json')||isNotEmpty(getResponseObj(item).errored,'propertyDescs')) ">
                              <div class="res-item-title">
                                  失败的响应
                              </div>
                              <div class="jsonView-container" v-if="formatJSon(getResponseObj(item).errored.json)!=null">
                                  <JsonView  :data="formatJSon(getResponseObj(item).errored.json)" :font-size="13" ></JsonView>
                              </div>
                              <template v-if="getResponseObj(item).errored.desced">
                                  <Divider orientation="left">属性说明</Divider>
                                  <Table border :disabled-hover="true" row-key="_id"  :columns="iviewTableResponseColumns" :data="formatTreeTableData(getResponseObj(item).errored.propertyDescs,'propertys')"></Table>
                              </template>
                              
                            </div>
                             
                        </div>
                    </div>

              </li>
    
          </ul>
      </div>

      <div class="page-info" style="text-align:center;margin:30px" v-if="apiQueryEntity.total>20" >
          <Page :total="apiQueryEntity.total" :page-size="20" @on-change="(newPage)=>{apiQueryEntity.pageNum=newPage;openActive=-1;loadApiInfo()}" show-total size="small" />
      </div>

      <div style="text-align:center;padding:40px" v-if="apiListData.length==0&&inited">
        <img src="@/assets/img/not_found.jpg" alt="" style="width:300px">
      </div>
    </div>
  </div>
</template>

<script>
import JsonView from "vue-json-views";
export default {
  name: "vie-info",
  components: {
    JsonView,
  },
  data() {
    return {
      inited:false,
      openActive: -1,
      viewEntity: {},
      versionActive: null,
      projectEntity: {},
      groupActive: "0",
      groupsArray: [],
      apiQueryEntity: {
        pageSize: 20,
        pageNum: 1,
        total: 0,
        entity: {
          projectVersionId: null,
          status: 1,
        },
        likeBox: {
          column: "api_url,api_desc",
          likeValue: "",
        },
      },
      apiListData: [],

      // 请求头说明的table 定义
      iviewTableHeadersColumns: [
        {
          title: "请求头名称",
          key: "key",
        },
        {
          title: "请求头值项",
          key: "value",
        },
      ],
      // url参数和query参数 的table头
      iviewTableParamsColumns: [
        {
          title: "参数名称",
          key: "name",
          tree: true,
        },
        {
          title: "参数类型",
          key: "type",
        },
        {
          title: "参数说明",
          key: "desc",
        },
        {
          title: "是否必填",
          render: (h, { row, index }) => {
            return h("span", row.required == 1 ? "是" : "否");
          },
        },
      ],
      // 响应属性说名的table头
      iviewTableResponseColumns: [
        {
          title: "属性名称",
          key: "name",
          tree: true,
        },
        {
          title: "属性类型",
          key: "type",
        },
        {
          title: "属性说明",
          key: "desc",
        },
      ],
    };
  },
  async created() {
    // 加载视图信息
    const result = await this.loadViewInfo(this.$route.params.viewId);
    let backFlag = true;
    // 检查视图是否有效
    if (result && result.length >= 1) {
      this.viewEntity = result[0];
      if (
        this.$route.query.hasPass == 0 ||
        this.viewEntity.showPass === this.$route.query.pass
      ) {
        backFlag = false;
      }
    }
    // 视图是否需要密码 并且密码正确
    if (backFlag) {
      this.$Message.warning("视图不存在或密码错误");
      history.back();
    }

    // 查询视图项目信息
    this.versionActive = this.viewEntity.versionList[0].versionId;
    const proObj = await this.loadProjectInfo(this.viewEntity.projectId);

    if (proObj == null) {
      this.$Message.warning("项目信息未找到");
      history.back();
      return;
    }
    this.projectEntity = proObj;

    // 加载API分组
    const groups = await this.loadVersionGroups(this.versionActive);
    console.log(groups);
    this.groupsArray = groups;

    //加载API集合
    this.loadApiInfo();

    this.inited=true;
  },
  watch: {
    versionActive: async function (newVal, oldVal) {
      const groups = await this.loadVersionGroups(newVal);
      this.groupActive = groups[0].id;
      this.groupsArray = groups;
      this.loadApiInfo();
    },
    groupActive: function (newVal, oldVal) {
      if (newVal !== "0") {
        this.apiQueryEntity.entity.groupId = newVal;
      } else {
        delete this.apiQueryEntity.entity.groupId;
      }
      this.loadApiInfo();
    },
  },
  methods: {
    generateExampleData(data){
      try{
          return JSON.parse(data.example);
      }catch(error){}
      return null;
    },
    isNotEmpty(obj, ...propertys) {
      if (!obj) {
        return false;
      }
      for (var p of propertys) {
        const prop = obj[p];
        if (!prop) {
          return false;
        }

        if (typeof prop === "string" && prop.length == 0) {
          return false;
        }

        if (
          typeof prop === "object" &&
          prop.constructor.name === "Array" &&
          prop.length == 0
        ) {
          return false;
        }
      }

      return true;
    },
    formatTreeTableData(jsonData, property) {
      if (!jsonData || jsonData.length == 0) {
        return [];
      }
      
      for (let item of jsonData) {
        item._id=item.name+"_"+Math.random()*new Date().getTime();
        if(item.type!=='Object'){
            continue;
        }
        //  增加children属性
        Object.defineProperty(item, "children", {
          enumerable: true,
          configurable: false,
          set: function formatTreeTableData_set(val) {
            item[property] = val;
          },
          get: function formatTreeTableData_get() {
            return item[property];
          },
        });
        item["_showChildren"] = true;

        if (item[property]) {
          this.formatTreeTableData(item[property], property);
        }
      }
      return jsonData;
    },
    formatJSon(jsonStr) {
      try {
        return JSON.parse(jsonStr);
      } catch (error) {}
      return null;
    },
    getResponseObj(item) {
      if (!item.resContent || item.resContent.length === "") {
        return {};
      }
      return JSON.parse(item.resContent);
    },
    getRequestParamObj(item) {
      if (!item.reqContent || item.reqContent.length === "") {
        return {};
      }
      return JSON.parse(item.reqContent);
    },
    getHeaders(item) {
      if (!item.reqHeaders || item.reqHeaders.length === "") {
        return [];
      }
      return JSON.parse(item.reqHeaders);
    },
    openAndCloseBox({row,index}) {
      // if(index===this.openActive){
      //   this.openActive = -1;
      // }else{
      //   this.openActive = index;
      // }
      this.$set(row,'closed', !row['closed'])
    },
    async loadVersionGroups(versionId) {
      try {
        const { data } = await this.$server.api.getGroupListByVersionId(
          versionId
        );
        if (data.code == 200) {
          return this.formatGroups(data.data);
        }
      } catch (error) {
        console.error(error);
      }
      return null;
    },
    async loadProjectInfo(projectId) {
      let data;
      try {
        const res = await this.$server.project.getProjectById(projectId);
        data = res.data;
        if (data.code == 200) {
          return data.data;
        }
      } catch (error) {
        console.error(error);
      }
      return null;
    },
    loadViewInfo(viewId) {
      var queryEntity = {
        entity: {
          id: viewId,
        },
      };
      return new Promise((resolve, reject) => {
        this.$server.views.queryViewDatas(queryEntity).then((res) => {
          const { data } = res;
          if (data.code == 200) {
            resolve(data.data.records);
          } else {
            reject(data.msg);
          }
        });
      });
    },
    formatGroups(datas) {
      const newArray = [
        {
          id: "0",
          groupName: "全部",
        },
      ];

      if (!datas || datas.length == 0) {
        return newArray;
      }

      for (var i in datas) {
        let obj = datas[i];
        newArray.push({
          id: obj.id,
          groupName: obj.groupName,
        });
      }
      return newArray;
    },
    exportClick() {
      this.$server.views.exportViewExcel(this.viewEntity.viewId).then((res) => {
        location.href = res.target;
      });
    },
    loadApiInfo() {
      this.apiQueryEntity.entity.projectVersionId = this.versionActive;
      this.$server.api.getApiPage(this.apiQueryEntity).then((res) => {
        var { data } = res;

        if (data.code == 200) {
          this.apiListData = data.data.records;
          console.log(this.apiListData);
          this.apiQueryEntity.total = data.data.total;
        } else {
          this.$Message.warning(data.msg);
        }
      });
    },
    groupNameFilter(val) {
      if (val === "0") {
        return "默认分组";
      }

      for (var item of this.groupsArray) {
        if (item.id === val) {
          return item.groupName;
        }
      }

      return "未知分组";
    },
  },
};
</script>

<style lang="scss" scoped>
@import url("../assets/css/animate.min.css");
.view-info {
  overflow-y: auto;
  width: 100%;
  min-height: 100vh;
  min-width: 1100px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  position: relative;
  background-color: #fff;

  .header {
    user-select: none;
    -moz-user-select: none;
    -webkit-user-select: none;
    background: #367fa9;
    color: #fff;
    box-shadow: 0 0 5px rgba($color: #000000, $alpha: 0.1);
    width: 100%;
    padding: 15px;
    height: 60px;
    text-align: center;

    .content {
      display: inline-block;
      position: relative;
      text-align: left;
      width: 1080px;
    }
    .title {
      position: relative;
      font-weight: 700;
      display: inline-block;
      padding-left: 40px;
      .icon {
        position: absolute;
        left: 0px;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        background-color: rgba($color: #000000, $alpha: 0.8);
        text-align: center;
        padding: 4px;
      }
      font-family: "Droid Sans", sans-serif;
      font-size: 20px;
    }

    .export-btn-box {
      padding-top: 4px;
      float: right;
      margin-left: 60px;
      button {
        border-radius: 3px;
        padding: 4px 15px;
        box-sizing: border-box;
        cursor: pointer;
        background-color: rgba($color: #000000, $alpha: 0.5);
        color: #fff;
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
          Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
        font-weight: 700;
        outline: none;
        border: none;
        transition: color 0.3s, background-color 0.3;
        &:hover {
          transition: color 0.3s, background-color 0.3s;
          background-color: rgba($color: #f1872f, $alpha: 1);
        }
      }
    }
    .versions {
      padding-top: 4px;
      float: right;
      select {
        min-width: 120px;
        height: 25px;
      }
    }
  }

  .container {
    width: 1080px;
    .desc-box {
      width: 100%;
      min-height: 200px;
      padding: 25px 5px;
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      .desc {
        font-size: 15px;
        text-shadow: 0 0 2px rgba(0, 0, 0, 0.2);
      }
      .content {
        display: flex;
        flex: row;
        justify-content: space-between;
        width: 100%;
      }
    }
  }

  .main-container {
    width: 100%;
    height: auto;
    padding: 6px;
    .api-items {
      list-style-type: none;
      width: 100%;
      li {
        padding: 6px 0;
        border-bottom: 1px solid #dddddd;

        &:nth-last-child(1):not(:nth-child(1)) {
          border-bottom: 1px solid transparent;
        }

        .item-info-simple-box {
          font-size: 18px;
          font-weight: 700;
          font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
          color: #999999;
          .title-box {
            user-select: none;
            display: inline-block;
            max-width: 650px;

            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;

            .title-first {
              cursor: pointer;
              &:hover {
                color: #000;
                text-decoration-line: underline;
              }
            }
          }

          .options-box {
            font-size: 14px;
            font-weight: 500;
            display: inline-block;
            float: right;

            .option-item {
              padding: 1px 12px;
              & + .option-item {
                border-left: 1px solid #ccc;
              }
            }
          }
        }
        .item-info-extra-box {
          width: 100%;
          min-height: 40px;
          background-color: rgb(245, 245, 245);
          overflow: hidden;
          display: flex;
          flex-direction: column;
          padding: 15px 8px;
          .extra-box-item {
            & + .extra-box-item {
              margin-top: 15px;
            }
            .full-title {
              background-color: rgb(255, 234, 230);
              display: block;
              padding: 5px 10px;
              font-size: 17px;
              font-weight: 500;
              position: relative;
              color: tomato;
              margin-bottom: 15px;
              margin-top: 15px;
              border-left: 3px solid tomato;
              border-radius: 3px;
            }

            .chil-title {
              padding: 6px 10px;
              position: relative;
              .example-data-link{
                text-decoration-line: none;
                margin-left:15px;
                position: relative;
                top:-1px
              }

              span {
                position: relative;
                top: 1px;
                display: inline-block;
                width: 15px;
                height: 15px;
                border-radius: 50%;
                border: 3px solid deepskyblue;
                background-color: transparent;
              }
              b {
                padding-left: 5px;
                font-size: 16px;
                font-weight: 700;
              }
            }

            .res-item-box {
              border-top: 3px solid transparent;
              &.color-green {
                border-top: 3px solid rgb(3, 122, 3);
              }
              &.color-error {
                border-top: 3px solid rgb(177, 0, 0);
              }

              padding: 10px;
              background-color: #fff;

              border-radius: 4px;
              box-shadow: 0 0 5px rgba($color: #000000, $alpha: 0.1);

              .jsonView-container {
                min-height: 60px;
                padding: 10px;
                border-radius: 4px;
                border: 1px solid rgba($color: #000000, $alpha: 0.1);
              }
              .res-item-title {
                width: 100%;
                padding: 2px 8px;
                margin-bottom: 12px;
                font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
                font-weight: 500;
                font-size: 15px;
                text-shadow: 0 0 1px rgba($color: #f01f1f, $alpha: 0.2);
                user-select: none;
              }
            }
          }
        }
      }
    }
  }
}

.title-item {
  padding: 1px 10px;
  border-left: 10px solid #367fa9;
  background-color: rgba($color: #367fa9, $alpha: 0.2);
  font-size: 16px;
}
</style>