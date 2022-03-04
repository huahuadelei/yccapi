<template>
    <div class="views">
        <ViewCounter :counter="counter" style="min-width:930px" />
        <MyCard title="视图列表" style="margin-top:20px;min-width:930px" :headerHeight="45">
            <div class="add-box">
                <span class="add-btn" @click="openBox">
                    <Icon type="md-add" size="18" />创建
                </span>
            </div>
            <template #extra>
               <div class="extra">
                 <RadioGroup v-model="queryEntity.entity.viewStatus" type="button" @on-change="statusChange">
                  <Radio :label="0">全部</Radio>
                  <Radio :label="1">使用中</Radio>
                  <Radio :label="2">已作废</Radio>
              </RadioGroup>
              <Input search  placeholder="请输入视图名称" v-model="queryEntity.entity.likeKey" @on-search="searchView" style="width:250px;display:inline-block;margin-left:15px"/>
               </div>
            </template>

            <ViewItemListBox :itemDatas="itemDatas" @export-btn-click="exportClick" @show-btn-click="showClick"/>
             <div style="text-align:right;padding:5px 15px">
               <Page :total="queryEntity.total" size="small" show-total @on-change="pageChange"/>
             </div>
        </MyCard>

        
    <Modal
      :footer-hide="true"
      :mask-closable="false"
      v-model="modalBox.show"
      title="创建视图"
      width="500"
    >
      <Form
         ref="my-form"
        :model="modalBox.entity"
        :rules="modalBox.ruleValidate"
        :label-width="80"
        style="margin-top: 20px"
      >
        <FormItem label="视图名称" prop="viewGroupName">
          <Input
            v-model="modalBox.entity.viewGroupName"
            placeholder="请输入视图名称"
          />
        </FormItem>

         <FormItem label="指定项目" prop="projectId">
            <Select v-model="modalBox.entity.projectId" @on-change="projectSelect">
                <Option v-for="item in projectList" :value="item.id" :key="item.id">{{ item.projectName }}</Option>
            </Select>
        </FormItem>

         <FormItem label="选择分支版本" v-if="modalBox.entity.projectId" >
            <Select v-model="modalBox.entity.versions" multiple>
                <Option v-for="item in versionList" :value="item.id" :key="item.id">{{ item.versionInfo }}</Option>
            </Select>
        </FormItem>

        <FormItem >
            <Checkbox v-model="modalBox.entity.hasPass" :true-value="1" :false-value="0">是否启用密码</Checkbox>
            <Checkbox v-model="modalBox.entity.autoGenerate"  v-if="modalBox.entity.hasPass&&modalBox.entity.hasPass===1" >随机生成密码</Checkbox>
        </FormItem>

        <FormItem label="查看密码" v-if="modalBox.entity.hasPass&&modalBox.entity.hasPass===1&&!modalBox.entity.autoGenerate">
             <Input
            v-model="modalBox.entity.showPass"
            placeholder="请输入查看密码"
          />
        </FormItem>

          <FormItem label="有效期">
            <RadioGroup v-model="modalBox.entity.expTime" type="button">
                <Radio label="">永久</Radio>
                <Radio label="D:7">7天</Radio>
                <Radio label="M:1">1月</Radio>
                <Radio label="Y:1">1年</Radio>
            </RadioGroup >
        </FormItem>
        
    
        <FormItem >
            <Checkbox v-model="modalBox.entity.isTestable" :true-value="1" :false-value="0">是否启用接口测试</Checkbox>
        </FormItem>

        <FormItem>
          <Button
            type="primary"
            style="margin-left: 80px"
            size="large"
           @click="saveViewData"
            :loading="modalBox.loading"
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
import ViewItemListBox from "@/components/ViewListItemBox";
import MyCard from "@/components/MyCard";
import ViewCounter from "@/components/ViewCounter";
export default {
  name: "views",
  components: {
    MyCard,
    ViewCounter,
    ViewItemListBox,
  },
  data() {
    return {
      modalBox: {
        show: false,
        loading: false,
        entity: {
          viewGroupName: null,
          projectId: null,
          versions: [],
        },
        ruleValidate: {
          viewGroupName: [
            {
              required: true,
              message: "视图名称不能为空",
              trigger: "blur",
            },
          ],
          projectId: [
            { required: true, message: "必须指定一个项目", trigger: "blur" },
          ],
        },
      },
      loading: false,
      counter: {
      },
      itemDatas: [],
      projectList: [],
      versionList: [],
      queryEntity:{
          total:0,
          pageNum:1,
          pageSize:10,
          entity:{
              viewStatus:1
          }
      }
    };
  },
  created() {

    setTimeout(() => {
        this.loadProjectList();
    }, 1);

    setTimeout(() => {
        this.loadViewList();
    }, 1);

    setTimeout(() => {
        this.loaderCounter();
    }, 1);
  },
  methods: {
    // 查看按钮点击
    showClick(scope){
      this.$router.push({
        name:"view-info",
        query:{
          hasPass:scope.row.hasPass,
          pass:scope.row.showPass
        },
        params:{
           viewId:scope.row.viewId
        }
      })
      console.log(scope)
    },
    // 导出按钮点击
   exportClick(scope){
     this.$server.views.exportViewExcel(scope.row.viewId).then(res=>{
          location.href=res.target

      })
    },
    pageChange(newPage){
      this.queryEntity.pageNum = newPage;
      this.loadViewList();
    },
    statusChange(index){
      this.loadViewList();
    },
    searchView(){
      this.queryEntity.pageNum=1;
      this.loadViewList();
    },
    loaderCounter(){
        this.$server.views.viewCounter().then(res=>{
          const {data} = res;
          console.log(typeof data)
          console.log(data)
            if(data.code == 200){
              this.$set(this,'counter',data.data)
                // this.counter = data.data;
            }else{
                this.$Message.warning(data.msg);
            }
        })
    },
    loadViewList(){
        this.$server.views.queryViewDatas(this.queryEntity).then(res=>{
            const {data} = res;
            if(data.code == 200){
                console.log(data);
                this.itemDatas = data.data.records;
                this.queryEntity.total=data.data.total;
            }else{
                this.$Message.warning(data.msg);
            }
        })
    },
    saveViewData() {
    
      const entity = this.modalBox.entity;
      this.$refs["my-form"].validate((valid) => {
        if (valid) {
          if (!entity.versions || entity.versions.length == 0) {
            this.$Message.warning("至少选择一个版本分支");
            return;
          }
          if (entity.hasPass === 1 && !entity.autoGenerate && (!entity.showPass||entity.showPass.length == 0)) {
               this.$Message.warning("需要设置一个访问密码");
               return;
          }

          this.$server.views.createViewData(entity).then(res=>{
              const {data} = res;
              if(data.code == 200){
                  this.modalBox.show = false;
                 this.$Message.success("创建成功");
                 this.loadViewList();
              }else{
                 this.$Message.warning(data.msg);
              }
          });
        }
      });
    },
    projectSelect(projectId) {
      console.log(projectId)
      this.$server.project.getProjectVersionList(projectId).then((res) => {
        const { data } = res;
        if (data.code == 200) {
          this.versionList = data.data;
          console.log(data.data);
        } else {
          this.$Message.warning(data.msg);
        }
      });
    },
    loadProjectList() {
      this.$server.project.getProjectList().then((res) => {
        const { data } = res;
        if (data.code == 200) {
          this.projectList = data.data;
        } else {
          this.$Message.warning(data.msg);
        }
      });
    },
    openBox() {
      this.modalBox.entity = {
        isExp: 0,
        hasPass: 0,
        autoGenerate: true,
        isTestable: 0,
        hasPass:0
      };
      this.modalBox.show = true;
    },
  },
};
</script>

<style lang="scss" scoped>
.views {
  background-color: #ecf0f5;
  padding: 10px;
  width: 100%;
  height: calc(100vh - 50px);
  overflow-x: auto;

  .add-box {
    width: 100%;
    min-height: 20px;
    padding: 2px;
    .add-btn {
      padding: 5px 15px;
      display: inline-block;
      border-width: 1px;
      border-style: dashed;
      color: #ccc;
      border-color: #ccc;
      width: 100%;
      text-align: center;
      user-select: none;
      cursor: pointer;
      &:hover {
        border-color: #367fa9;
        color: #367fa9;
      }
    }
  }
}
.extra{
  width: 500px;
  padding-top: 5px;
}
</style>