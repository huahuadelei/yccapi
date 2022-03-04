<template>
  <div class="show-view">
    <div class="manager-page-btn" @click="$router.push({ name: 'home' })">
      <p><Icon type="md-settings" size="25" style="margin-bottom: 8px" /></p>
      <p>管理页面</p>
    </div>

    <div class="content">
      <div class="title-box">
        <img
          src="@/assets/img/tansuo.png"
          style="width: 90px; transform: translateY(26px)"
        /><span>Explore</span>
      </div>

      <div class="search-box">
        <MySearch
          v-model="queryEntity.entity.likeKey"
          @on-search="onSearch"
          placeholder="请输入视图名称或项目名称"
        />
      </div>

      <div class="view-item-box">
            <ViewItemListBox :itemDatas="itemDatas" @export-btn-click="exportClick"  @show-btn-click="showClick"/>
      </div>
      <div class="floor-tools" v-if="queryEntity.total!=0&&Math.ceil(queryEntity.total*1.0/queryEntity.pageSize) !== 1">
          <Page :total="queryEntity.total"  @on-change="changePageNum"/>
      </div>
    </div>
  </div>
</template>

<script>
import MySearch from "@/components/MySearch";
import ViewItemListBox from "@/components/ViewListItemBox";
export default {
  name: "show-view",
  components: {
    MySearch,
    ViewItemListBox
  },
  data() {
    return {
      value2: 0,
      itemDatas: [],
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
      this.loadViewList();
  },
  methods: {
    changePageNum(newPage){
        this.queryEntity.pageNum=newPage;
        this.loadViewList();
    },
    onSearch() {
      this.queryEntity.pageNum=1;
        this.loadViewList();
    },
    loadViewList(){
        this.$server.views.queryViewDatas(this.queryEntity).then(res=>{
            const {data} = res;
            if(data.code == 200){
                console.log(data);
                this.itemDatas = this.formatPass(data.data.records);
                this.queryEntity.total=data.data.total;
            }else{
                this.$Message.warning(data.msg);
            }
        })
    },
    exportClick(scope){
       var readShowPass;
      if(scope.row.hasPass){
        readShowPass = prompt('请输入访问密码','');
        if(!readShowPass){
          return ;
        }
        if(readShowPass !== scope.row._showPass){
          this.$Message.warning('密码输入错误');
          return;
        }
      }
     this.$server.views.exportViewExcel(scope.row.viewId).then(res=>{
          location.href=res.target

      })
    },
     // 查看按钮点击
    showClick(scope){

       var readShowPass;
      if(scope.row.hasPass){
        readShowPass = prompt('请输入访问密码','');
        if(!readShowPass){
          return ;
        }
        if(readShowPass !== scope.row._showPass){
          this.$Message.warning('密码输入错误');
          return;
        }
      }

      this.$router.push({
        name:"view-info",
        query:{
          hasPass:scope.row.hasPass,
          pass:scope.row._showPass
        },
        params:{
           viewId:scope.row.viewId
        }
      })
    },

   formatPass(datas){
    if(!datas || datas.length == 0){
      return datas;
    }

    for(var i in datas){
      const obj = datas[i];
      if(obj.hasPass===1){
        obj._showPass=obj.showPass;
        obj.showPass = '需要密码'
      }else{
         obj.showPass = '无需密码';
      }
    }
    return datas;

  }


  }, // methods end
 
};
</script>

<style lang="scss" scoped>
@import "../assets/css/animate.min.css";
.show-view {
  min-width: 900px;
  height: 100%;
  width: 100%;
  position: relative;
  overflow-y: auto;



  & > .content {
    padding: 20px;
    height: inherit;
    min-width: 950px;

    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-items: center;

    .title-box {
      font-size: 43px;
      font-weight: 700;
      margin: 30px;
      user-select: none;
    }
  }

  .manager-page-btn {
    position: absolute;
    text-align: center;
    cursor: pointer;
    font-weight: 700;
    font-size: 12px;
    width: 65px;
    height: 65px;
    border-radius: 4px;
    border: 1px solid transparent;
    padding-top: 6px;
    top: 15px;
    right: 15px;
    border: 1px solid #ccc;

    &:hover {
      border-color: #367fa9;
      color: #367fa9;
    }
  }

  .view-item-box {
    width: inherit;
    min-width: 1024px;
    margin-top: 2%;
  }
  .floor-tools{
      padding: 30px;
      width: 900px;
      text-align: center;
      font-size: 16px;
     
  }
}

</style>