<template>
  <div class="role-rule">

       <Modal
            :footer-hide="true"
            :mask-closable="false"
            v-model="modalBox.show"
            :title="modalBox.title"
            width="500"
            >
            <Form :model="roleEntity"  :label-width="80" style="margin-top: 20px">
                
                <FormItem label="角色名称" >
                    <Input v-model="roleEntity.roleName" placeholder="请输入角色名称" />
                </FormItem>

                <FormItem label="角色编码" >
                    <Input v-model="roleEntity.roleCode" placeholder="请输入角色编码" />
                </FormItem>

                <FormItem label="描述信息" >
                    <Input v-model="roleEntity.roleDesc" type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="输入描述信息"></Input>
                </FormItem>

               
                <FormItem >
                    <Button type="primary" style="margin-left:80px" size="large" @click="submitData">保存</Button>
                   <Button type="default" style="margin-left:25px" size="large" @click="modalBox.show=false">取消</Button>
                </FormItem>
                
            </Form>
            </Modal>



      <Card class="def-box-shadow " style="height:72px;margin-bottom:15px">

          
          <Button icon="md-add" type="info" size="large" @click="addRole">
              添加
          </Button> 

          <Button icon="ios-create" style="margin-left:15px" type='error' size="large"
           :disabled="!selectEntity||selectEntity.roleCode==='Administrator'"
            @click="deleteRole">
              删除
          </Button>

          <Button icon="ios-create" style="margin-left:15px" size="large"   :disabled="!selectEntity||selectEntity.roleCode==='Administrator'" @click="editRole">
              编辑
          </Button>
      </Card>

    <Card class="def-box-shadow auto_height">
      
      <p slot="title">角色配置</p>

      <div class="box-content">
        <div class="box-top">
          <div class="box-item" style="flex: 1">
           
            <ul class="list-group">
              <li v-for="item in roleList" 
                    :key="item.id" 
                    class="list-group-item"
                    :class="[item.id == selectEntity.id?' item-active':'']"
                    @click="roleActive(item)"
               >
                <div class="icon">
                    <Icon type="md-person" size="50"/>
                </div>
                <div class="content">
                    
                  <div class="title">
                   {{item.roleName}}
                  </div>
                  <div class="desc" v-if="item.roleDesc">{{item.roleDesc}}</div>
                   <div class="desc" v-if="!item.roleDesc">该角色没有描述信息</div>
                </div>

            
              </li>
              
            </ul>
          </div>
          <div class="box-item" style="flex: 1; padding: 15px">
            <Tree :data="rolePerms"  ref="roleTree" show-checkbox></Tree>
          </div>
        </div>

        <div class="box-bottom">
          <Button type="primary" size="large" @click="saveData">保存</Button>
          <Button size="large" @click="loadRoleMenuPerms">重置</Button>
        </div>
      </div>
    </Card>

    <MyLoading :show="loading" type="3" text="数据准备中..."></MyLoading>
  </div>
</template>

<script>
import MyLoading from '@/components/MyLoading'
export default {
  name: "role-rule",
  components:{
      MyLoading
  },
  data() {
    return {
      loading:true,
      roleEntity:{
            loading:true

      },  
      modalBox:{
          show:false,
          title:"新增角色"
      },
      selectEntity:{
          id:"1"
      },
      menuPermTree:[],
      roleList:[],
      rolePerms: [],
    };
  },
  created() {
      this.loadMenuTree();
      // 加载角色列表
      this.loadRoleList((list)=>{
          if(list&&list.length>0){
            this.selectEntity = list[0];
            //加载角色菜单
             setTimeout(() => {
                this.loadRoleMenuPerms();
            }, 1);
            this.loading=false;
          }
      });
  },
  methods: {
   addRole(){
       this.roleEntity={};
       this.modalBox={
           show:true,
           title:"新增角色"
       }
   },
   editRole(){
      this.roleEntity = {...this.selectEntity};
       this.modalBox={
           show:true,
           title:"编辑角色"
       }

   },
   submitData(){
       if(!this.roleEntity||!this.roleEntity.roleName||this.roleEntity.roleName.trim()===''){
             this.$Message.warning({
                    content:"角色名称必须填写",
                    duration:2
                });
           return ;
       }
        if(!this.roleEntity.roleCode||this.roleEntity.roleCode.trim()===''){
             this.$Message.warning("角色编码必须填写");
           return ;
       }
       this.$server.role.saveRoleEntity(this.roleEntity).then(res=>{
           const data = res.data;
             if(data.code == 200){
                    this.loadRoleList();
                    this.$Message.success(data.msg);
                    this.modalBox.show=false;
                }else{
                    this.$Message.warning({
                        content:data.msg,
                        duration:2
                    });
                }
       })


   },
    // 保存角色权限信息  
    saveData(){
        var checkedData = this.$refs.roleTree.getCheckedAndIndeterminateNodes();
        const newArray = checkedData.map(item=>item.id);

          this.$Modal.confirm({
                title: "更新",
                content: `<p>是否要更新选中角色的权限信息?</p>`,
                loading: true,
                onOk: () => {
                
                  let data = {
                      menuIds:newArray,
                      roleId:this.selectEntity.id
                  }

                  this.$server.role.updateSelectMenus(data).then(res=>{
                        const {data} = res;
                        if(data.code == 200){
                            this.$Modal.remove();
                            this.$Message.success(data.msg);
                        }else{
                            this.$Message.warning({
                                content:data.msg,
                                duration:2
                            });
                        }
                    })
        
                }
           });

        
    },
    // 删除角色
    deleteRole(){
        this.$Modal.confirm({
                title: "删除",
                content:
                `<p>是否删除[${this.selectEntity.roleName}]这个角色?</p>`,
                loading: true,
                onOk: () => {
                this.$server.role.deleteRole(this.selectEntity.id).then((res) => {
                    const data = res.data;

                    if (data.code == 200) {

                      this.$Modal.remove();
                      this.loadRoleList();
                      this.$Message.info('删除成功')
                    } else {
                    this.$Modal.remove();

                    this.$Message.warning({
                        content: data.msg,
                        duration: 5,
                    });
                    }
                });
                },
            });
    },
    //点击角色获取对应角色的菜单
    roleActive(item){
        if(item.id == this.selectEntity.id){
            return ;
        }
        this.selectEntity = item;
        this.loadRoleMenuPerms();

    },
      // 获取角色列表
    loadRoleList(callback){
        this.$server.role.getRoleList().then(res=>{
            const {data} = res;
            if(data.code == 200){
                this.roleList = data.data;
                if(callback){
                    callback(data.data);
                }
            }else{
                  this.$Message.warning({
                    content:data.msg,
                    duration:2
                });
            }
        })
    },
    loadMenuTree(){
        this.$server.menu.getTree().then(res=>{
            const {data} = res;
             if(data.code == 200){
              this.menuPermTree = data.data;
            }else{
                this.$Message.warning({
                    content:data.msg,
                    duration:2
                });
            }
        })
    },
    // 加载角色的菜单权限
    loadRoleMenuPerms() {
      this.$server.role.getRoleMenuPermsOptions(this.selectEntity.id).then(res=>{
          var data = res.data;
          if(data.code == 200){
             this.buildRolePermTreeData(data.data,this.menuPermTree);
          }else{
                this.$Message.warning({
                    content:data.msg,
                    duration:2
                });
          }
      })
    },
    // 构建角色选中的数据树
    buildRolePermTreeData(selectDatas,menuTree){
        let map = new Map();
        if(selectDatas){
            for(var i in selectDatas){
                map.set(selectDatas[i].id,selectDatas[i]);
            }   
        }

        this.rolePerms= this.doBuildRoleTree(map,menuTree);

    },
    doBuildRoleTree(map,menuTree){
        if(!menuTree||menuTree.length==0){
            return ;
        }
        let hasExpand = function(item){
            const data = map.get(item.menuId);
            if(data&&!item.isParent){
                return true;
            }else{
                return false;
            }
        }

        const newArray = [];
        for(var i in menuTree){
            let item = menuTree[i];
            const newObj = {
                title:item.menuName,
                id:item.menuId,
                 expand:true,
                checked: hasExpand(item),
                children:this.doBuildRoleTree(map,item.children)
            }
            newArray.push(newObj);
        }
        return newArray;
    }

  },
};
</script>

<style lang="scss" scoped>
.role-rule {
  min-width: 700px;
overflow-y: auto;
  width: 100%;
  height: calc(100vh - 50px);
  padding: 15px;
  position: relative;

  .auto_height {
    height: calc(100% - 85px);
    min-height: 500px;

    & > * {
      height: inherit;
    }
  }

  .box-content {
    display: flex;
    position: relative;
    flex-direction: column;
    justify-content: flex-start;
    height: inherit;

    .box-top {
      display: flex;
      flex-direction: row;
      flex: 1;
      height: inherit;
      .box-item {
        border: 1px solid rgba($color: #000000, $alpha: 0.05);
        min-width: 280px;
        overflow-y: auto;
        overflow-x:hidden;
        background-color: rgb(255, 255, 255);
      }
    }
    .box-bottom {
      text-align: center;
      transform: translateY(50px);
      * + * {
        margin-left: 20px;
      }
    }
  }

  .list-group {
    list-style-type: none;
    width: 100%;
    height: auto;
    .list-group-item {
      padding: 12px;
      background-color:rgba($color:#ffffff,$alpha:1);
      border-bottom: 1px solid rgba($color: #000000, $alpha: 0.05);
      height: 80px;
      width: inherit;
      cursor: pointer;
      position: relative;
      display: flex;
      flex-direction: row;
      justify-content: flex-start;
      &.item-active{
          background-color: #F5F5F5 !important;
      }

      &:hover {
          background-color: #F5F5F5 !important;
        .btn-group{
            a{
                display: inline;
            }
        }
      }
       .icon{
           flex: .1;
           line-height: 70px;
       }
      .content {
        flex: 1;
        .title {
          font-size: 18px;
          font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", 微软雅黑, Arial, sans-serif;
          font-weight: 700;
          line-height: 35px;
        
        }
        .desc {
            font-size: 13px;
        }
      }
    }
  }
}
.ivu-cell {
  height: 80px;
  & * {
    height: inherit !important;
  }
}
.ivu-card-head {
  padding: 15px !important;
}
</style>