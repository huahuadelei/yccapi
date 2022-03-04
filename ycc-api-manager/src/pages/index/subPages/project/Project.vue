<template>
    <div class="projects">
     <Card class="def-box-shadow" style="height:75px; margin-bottom: 15px">
        <Col :span="3">
            <Button icon="md-add" size="large" type="primary" @click="projectEntity={};modalBox={title:'添加',show:true}">
                添加项目
            </Button>
        </Col>
        <Col :span="7" offset="14">
            <Input
            size="large"
            @on-search="()=>{ queryEntity.pageNum=1;loadProjectList();}"
             search 
             enter-button
              placeholder="请输入项目名称进行搜索" 
              v-model="queryEntity.entity.projectName"/>
        </Col>
    </Card>

    <Card class="def-box-shadow auto_height">
      <Table
        row-key="id"
        :loading="tableModel.loading"
        :columns="tableModel.columns"
        :data="tableModel.columnDatas"
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
      v-model="modalBox.show"
      :title="modalBox.title"
      width="550"
    >
      <Form :model="projectEntity" :label-width="100" style="margin-top: 20px">
        <FormItem label="项目名称">
          <Input
            v-model="projectEntity.projectName"
            placeholder="项目名称"
          />
        </FormItem>



        <FormItem label="项目描述">
          <Input
           maxlength="140" show-word-limit
          type="textarea" :autosize="{minRows: 2,maxRows: 5}"
            v-model="projectEntity.projectDesc"
            placeholder="请输入项目描述"
          />
        </FormItem>

        <FormItem label="开发环境地址">
          <Input
            v-model="projectEntity.profileDevUrl"
            placeholder="开发环境地址"
          />
        </FormItem>

        <FormItem label="测试环境地址" >
          <Input
            v-model="projectEntity.profileTestUrl"
            placeholder="测试环境地址"
          />
        </FormItem>

        <FormItem>
          <Button
            type="primary"
            style="margin-left: 80px"
            size="large"
            @click="saveData"
            >保存</Button
          >
          <Button
            type="default"
            style="margin-left: 25px"
            size="large"
            @click="modalBox.show = false"
            >取消</Button
          >
        </FormItem>
      </Form>
    </Modal>

    </div>
</template>

<script>
    export default {
        name: 'projects',
        components:{
        },
        data() {
            return {
               modalBox: {
                    show: false,
                    title: "添加项目",
                },
                projectEntity:{

                },
                 queryEntity: {
                    total: 0,
                    pageNum: 1,
                    pageSize: 10,
                    entity: {},
                },
                 tableModel: {
                    loading: false,
                    columns: [
              
                    {
                        title: "项目名称",
                        key: "projectName",
                    },
                    {
                        title: "项目描述",
                        key:"projectDesc"
                    },
                    {
                        title: "开发环境地址",
                        key:"profileDevUrl"
                    },
                     {
                        title: "测试环境地址",
                        key:"profileTestUrl"
                    },
                    {
                        title: "最后更新时间",
                        key: "updateTime",
                    },
                    {
                        title: "操作",
                        width: 220,
                        render: (h, scope) => {
                        const btn_group_array = [];

                        btn_group_array.push(
                            h(
                            "Button",
                            {
                                props: {
                                type: "info",
                                size: "small",
                                ghost: true,
                                },
                                on: {
                                click: () => {
                                    this.updateProject(scope.row);
                                },
                                },
                            },
                            "编辑"
                            )
                        );


                            btn_group_array.push(
                            h(
                                "Button",
                                {
                                style: {
                                    "margin-left": "10px",
                                },
                                props: {
                                    type: "error",
                                    size: "small",
                                    ghost: true,
                                },
                                on: {
                                    click:()=>{
                                        this.deleteProject(scope.row);
                                    },
                                },
                                },
                                "删除"
                            )
                            );

                               btn_group_array.push(
                            h(
                                "Button",
                                {
                                style: {
                                    "margin-left": "10px",
                                },
                                props: {
                                    type: "warning",
                                    size: "small",
                                    ghost: true,
                                },
                                on: {
                                    click:()=>{
                                     this.showProject(scope.row);
                                    },
                                },
                                },
                                "进入项目"
                               )
                            );

                             return h("div", btn_group_array);
                        },
                    },
                    ],
                    columnDatas: [],
                },
            }
        },
        created(){
            this.loadProjectList();
        },
        methods:{
            saveData(){
               const entity = this.projectEntity;
               if(!entity||!entity.projectName||entity.projectName.trim() ===''){
                    this.$Message.warning('项目名称不能为空')
                   return;
               }
                if(entity.id){
                    this.$server.project.updateProject(entity).then(res=>{
                         const data = res.data;

                        if (data.code == 200) {
                            this.loadProjectList();
                            this.modalBox.show=false;
                            this.$Message.info("修改成功");
                        } else {
                            this.$Message.warning({
                                content: data.msg,
                                duration: 2,
                            });
                        }
                    })
                }else{
                    this.$server.project.saveProject(entity).then(res=>{
                         const data = res.data;

                        if (data.code == 200) {
                            this.loadProjectList();
                            this.modalBox.show=false;
                            this.$Message.info("添加成功");
                        } else {
                            this.$Message.warning({
                                content: data.msg,
                                duration: 2,
                            });
                        }
                    })
                }
            },
            updateProject(row){
                this.projectEntity = Object.assign({},row);
                this.modalBox={
                    title:"修改项目信息",
                    show:true
                };

            },
            deleteProject(row){
                 this.$Modal.confirm({
                    title: "删除",
                    content:
                    `<p>是否删除<b style="color:red">${row.projectName}</b>这个项目?</p>`,
                    loading: true,
                    onOk: () => {
                    this.$server.project.deleteProject(row.id).then((res) => {
                        const data = res.data;

                        if (data.code == 200) {
                        this.loadProjectList();
                        this.$Modal.remove();
                        this.$Message.info("删除成功");
                        } else {
                        this.$Message.warning({
                            content: data.msg,
                            duration: 2,
                        });
                        }
                    });
                },
            });
            },
            showProject(row){

                this.$router.push({
                    name:"api-list",
                    query:{
                        projectId:row.id
                    }
                })
            },
            changePage(newPage){
                this.queryEntity.pageNum=newPage;
                this.loadProjectList();
            },
            changePageSize(newSize){
                this.queryEntity.pageSize=newSize;
                this.loadProjectList();
            },
            loadProjectList(){
                this.tableModel.loading=true;
                this.$server.project.queryProjectPage(this.queryEntity).then(res=>{
                    const {data} =  res;

                    this.tableModel.loading=false;
                    if(data.code == 200){
                        this.queryEntity.total=data.data.total
                        this.tableModel.columnDatas = data.data.records
                    }else{
                        this.$Message.warning(data.msg)
                    }
                })
            }
        }
    }
</script>

<style lang="scss" scoped>
  .projects{
      height: calc(100vh - 50px);
      overflow-y: auto;
      width: 100%;
      min-width: 750px;
      padding: 15px;
      position: relative;

       
  }  

 
</style>