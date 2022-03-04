<template>
    <div class="sys_log">
    
          <Card class="def-box-shadow" style="height: 60px; margin-bottom: 15px">
            <Col :span="4">
                  <Input
                    placeholder="日志详情" 
                    v-model="queryEntity.entity.logInfo"/>
            </Col>
            <Col :span="4" offset="1">
                 <Input
                    placeholder="请求地址" 
                    v-model="queryEntity.entity.reqUrl"/>
            </Col>
             <Col :span="4" offset="1">
                 <!-- <Input
                    placeholder="运行状态" 

                    v-model="queryEntity.entity.successed"/> -->
                    <Select v-model="queryEntity.entity.successed" placeholder="请选择执行状态">
                        <Option :value="-1" >全部</Option>
                        <Option :value="1" >成功</Option>
                        <Option :value="0" >失败</Option>
                    </Select>
            </Col>
             <Col :span="5" offset="1">
                   <DatePicker 
                   format="yyyy-MM-dd" 
                   confirm 
                   type="daterange"
                   @on-change="dateBoxOnChange"
                    placement="bottom-end" 
                    placeholder="请选择时间范围" 
                   ></DatePicker>
            </Col>
             <Col :span="2" offset="1">
                <Button type="info" @click="()=>{queryEntity.pageNum=1;loadSysLogsList()}">搜索内容</Button>
            </Col>

        </Card>

        <Card class="def-box-shadow auto_height">
        <Table
            row-key="id"
            :columns="tableModel.columns"
            :data="tableModel.columnDatas"
            border
        ></Table>
        <div style="text-align:right;padding:15px 0">
            <Page 
            :total="queryEntity.total" 
            size="small" 
            :page-size="queryEntity.pageSize"
            show-total 
            show-sizer  
            :current="queryEntity.pageNum"
            @on-change="changePage"
            @on-page-size-change="changePageSize"
            />
        </div>
    </Card>


    <Modal 
     :footer-hide="true"
      :mask-closable="false"
      v-model="isShowInfoBox"
      title="日志详情"
      width="800"
    >

    <Row>
        <Col span="24">
            <p>
                 <span class="text-weight">基本信息</span>
                <Divider size="small" style="margin-top:4px;margin-bottom:4px"/>
            </p>
            <div class="info-content">
               <Form :label-width="100">
                    <FormItem label="日志详情" style="margin-bottom:5px">
                        <Col span="5">
                           <Tag type="dot" :color="logInfoObj.successed?'success':'error'" >{{logInfoObj.logInfo}}</Tag>

                        </Col>

                         <Col span="12" offset="7">
                           操作用户
                          <Tag color="cyan" >{{logInfoObj.username}}</Tag>
                        </Col>
                        
                    </FormItem>
                   
                    <FormItem label="IP地址" style="margin-bottom:5px"    >
                     

                        <Col span="5">
                             <Tag   >{{logInfoObj.ipAddr}}</Tag>
                        </Col>

                         <Col span="12" offset="7">
                           调用时长
                          <Tag color="geekblue" >{{logInfoObj.invokeTimes+'毫秒'}}</Tag>
                        </Col>
                    </FormItem>

                    <FormItem label="调用方法" style="margin-bottom:5px"    >
                        <Col span="10">
                             {{logInfoObj.classMethod}}
                        </Col>
                    </FormItem> 

                     <FormItem label="记录时间" style="margin-bottom:5px"    >
                        <Col span="10">
                             {{logInfoObj.createTime}}
                        </Col>
                    </FormItem> 
                </Form>
            </div>
          
        </Col>

        <Col span="24" style="margin-top:20px">
            <p>
                 <span class="text-weight">请求信息</span>
                <Divider size="small" style="margin-top:4px;margin-bottom:4px"/>
            </p>
            <div>
                <Form :label-width="100">
                    <FormItem label="请求地址" style="margin-bottom:5px">
                        <Col span="20">
                           <a :href="logInfoObj.reqUrl">{{logInfoObj.reqUrl}}</a>
                        </Col>
                    </FormItem>

                    <FormItem label="请求方式" style="margin-bottom:5px">
                       <Col span="20">
                           <Tag  color="blue" >{{logInfoObj.reqMethod}}</Tag>
                        </Col>
                    </FormItem>

                     <FormItem label="请求头信息" style="margin-bottom:5px"    >
                        <Col span="23">
                            <JsonView :data="getJson(logInfoObj.reqHeader)" :font-size="13" closed></JsonView>
                        </Col>
                    </FormItem> 

                     <FormItem label="请求数据" style="margin-bottom:5px"    >
                        <Col span="23">
                            <JsonView :data="getJson(logInfoObj.reqData)" :font-size="13" ></JsonView>
                        </Col>
                    </FormItem> 
                </Form>
            </div>
        </Col>

        <Col span="24" style="margin-top:20px" v-if="logInfoObj.successed==1">
            <p>
                 <span class="text-weight">响应信息</span>
                <Divider size="small" style="margin-top:4px;margin-bottom:4px"/>
            </p>
            <div>
                  <Form :label-width="100">
                     <FormItem label="响应数据" style="margin-bottom:5px"    >
                        <Col span="23">
                            <JsonView :data="getJson(logInfoObj.respData)" :font-size="13" ></JsonView>
                        </Col>
                    </FormItem> 
                </Form>
            </div>
        </Col>

         <Col span="24" style="margin-top:20px" v-if="logInfoObj.successed==0">
            <p>
                 <span class="text-weight">异常信息</span>
                <Divider size="small" style="margin-top:4px;margin-bottom:4px"/>
            </p>
            <div>
                <Form :label-width="100">
                    <Button @click="openErrorMsg(logInfoObj.errorMsg)">点击查看异常信息</Button>
                </Form>
            </div>
        </Col>
    </Row>


    </Modal>

    <my-loading :show="loading" type="6"></my-loading>
    </div>
</template>

<script>
import JsonView from 'vue-json-views'
import MyLoading from '@/components/MyLoading'
    export default {
        name: 'sys_log',
        components:{
            MyLoading,
            JsonView
        },
        data() {
            return {
                loading:true,
                queryEntity:{
                    total:0,
                    pageNum:1,
                    pageSize:10,
                    entity:{
                        startDate:"",
                        endDate:""
                    }
                },
                tableModel:{
                    columns:[
                        {
                            title:"日志详情",
                            key:"logInfo"
                        },
                        {
                            title:"IP地址",
                            key:"ipAddr"
                        },
                        {
                            title:"用户名",
                            key:"username"
                        },
                        {
                            title:"请求地址",
                            key:"reqUrl"
                        },
                        {
                            title:"执行时长",
                            key:"invokeTimes",
                            render(h,params) {
                                return h('Tag',{
                                    props:{
                                        color:"geekblue"
                                    }
                                },params.row.invokeTimes+"毫秒")
                            },
                        },
                        {
                            title:"执行状态",
                            width:"100",
                            key:"successed",
                            render:(h,params)=> {
                               const bool= params.row.successed===1;
                                return h('Tag',{
                                    props:{
                                        color:(bool?'success':'error')
                                    }
                                },bool?'成功':'失败')
                            },
                        },
                        {
                            title:"创建时间",
                            key:"createTime"
                        }
                        ,
                        {
                            title:"操作",
                            width:"100",
                            render:(createElement,params)=>{
                                const elementArray = [];

                                elementArray.push(
                                    createElement('Button',{
                                        props:{
                                            type:"info",
                                            ghost:true,
                                            long:true
                                        },
                                        on:{
                                            click:()=>{
                                                this.showInfo(params.row);
                                            }
                                        }
                                    },"详情")
                                )

                                return createElement('div',elementArray);
                            }
                        },

                    ],
                    columnDatas:[]

                },
                isShowInfoBox:false,
                logInfoObj:{}
            }
        },
        created() {
            this.loadSysLogsList();
        },
        methods: {
            openErrorMsg(err){
                var html = `<html>
                    <head>
                        <title>异常信息</title>
                    </head>
                    <body style="background-color:#FFFAE8;color:#248C85">
                        <div style="font-size:13px;">
                            ${err}
                        </div>
                    </body>
                </html>`;
                const newWindow = window.open('www', "_blank",'');
                newWindow.document.write(html);
                newWindow.focus();
                
            },
            getJson(text){
                if(!text||(typeof text)!='string'){
                    return {};
                }
                var json = JSON.parse(text);
                return json;

            },
            showInfo(row){
                this.logInfoObj=row;
                this.isShowInfoBox = true


            },
            dateBoxOnChange(dateArray,d){
                this.queryEntity.entity.startDate = dateArray[0];
                this.queryEntity.entity.endDate =  dateArray[1];
            },
            loadSysLogsList(callback){
                this.loading=true;
                this.$server.log.getSysLogList(this.queryEntity).then(res=>{
                    const {data} = res;
                    this.loading = false;
                    if(data.code == 200){
                        this.queryEntity.total = data.data.total;
                        this.tableModel.columnDatas=data.data.records;
                    }else{
                        this.$Message.warning(data.msg);
                    }
                })
            },
            changePage(currentPage){
                this.queryEntity.pageNum=currentPage;
                this.loadSysLogsList();

            },
            changePageSize(newPageSize){
                this.queryEntity.pageSize = newPageSize;
                this.loadSysLogsList();

            }
        },
    }
</script>

<style lang="scss" scoped>
    .sys_log{
        height: calc(100vh - 50px);
        padding: 15px;
        overflow-y: auto;
         .auto_height {
            height: calc(100% - 85px);
            min-height: 500px;
            overflow-y:auto;
            overflow-x:hidden;

            & > * {
                height: inherit;
            }
        }

       
    }

     .info-content{
        padding: 5px 5px;
    }

</style>