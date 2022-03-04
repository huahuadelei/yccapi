<template>
    <div class="index">
         <Header height="50px" bgColor="#3C8DBC" >
             <div class="logo-box text-not-select">
          
                    <span style="cursor: pointer;" @click="toHomePage">
                        API管理平台
                    </span>

              </div>

        
            <div class="login-user-box text-not-select">
                 <Dropdown 
                    v-if="$store.getters.getLoginUser.user&&$store.getters.getLoginUser.user.username"
                    @on-click="downMenuClick"
                    trigger="click"
                 >
                    {{$store.getters.getLoginUser.user.username}}
                    <Icon type="ios-arrow-down"></Icon>
                    <DropdownMenu slot="list" >
                        <DropdownItem disabled>个人信息</DropdownItem>
                        <DropdownItem name="repass">修改密码</DropdownItem>
                        <DropdownItem name="logout" divided>退出</DropdownItem>
                    </DropdownMenu>
                </Dropdown>
            </div>

            <div class="contact-box text-not-select" @click="linkManager">
                <Icon type="md-mail" />
                联系管理员
            </div>
         </Header>
         <div class="main-box">
              <Menu 
                ref="menu"
                 width="230px"
                    theme="dark" 
                    :active-name="menuActiveName"
                    class="my-menu text-not-select"
                    style=" height:inherit;"
                    @on-select="menuSelect"
                >

                    <template  v-for="item in $store.getters.getLoginUser.menuNodes">
                        <Menu-group :key="item.menuId" :title="item.menuName">
                            <template v-for="child in item.children" >
                                <Menu-item :key="child.menuId" :name="child.routePath" v-if="child.isParent==false">
                                    <Icon  type="md-radio-button-off" class="text-weight" :color="child.menuTheme" />
                                    {{child.menuName}}
                                </Menu-item>
                            </template>
                        </Menu-group>
                    </template>
                </Menu>

                <div class="main-right">
                    <keep-alive>
                        <router-view v-if="$route.meta.keepAlive"></router-view>
                    </keep-alive>
                         
                  <transition
                        :duration="{ leave: 300 }"
                          enter-active-class="animated fadeInRight"
                          leave-active-class="animated fadeOutRight"
                           mode="out-in">
                             
                        <router-view v-if="!$route.meta.keepAlive"></router-view>

                  </transition>

                </div>
         </div>
       


         <Modal
            :footer-hide="true"
            :mask-closable="false"
            v-model="modalBox.show"
            :title="modalBox.title"
            width="500"
            >
            <Form :model="modalBox.entity"  :label-width="80" style="margin-top: 20px">
                
                <FormItem label="原密码" >
                    <Input v-model="modalBox.entity.oldPass" type="password" password  placeholder="请输入原密码" />
                </FormItem>

                <FormItem label="新密码" >
                    <Input v-model="modalBox.entity.password" type="password" password  placeholder="请输入新密码" />
                </FormItem>

                
                <FormItem label="确认密码" >
                    <Input v-model="modalBox.entity.newPassTow" type="password" password  placeholder="请再次输入新密码" />
                </FormItem>

               
                <FormItem >
                    <Button type="primary" style="margin-left:80px" size="large"  @click="saveRePass()">保存</Button>
                   <Button type="default" style="margin-left:25px" size="large" @click="modalBox.show=false">取消</Button>
                </FormItem>
                
            </Form>
          </Modal>

            <Modal
            :footer-hide="true"
            :mask-closable="false"
            v-model="modalBox2.show"
            :title="modalBox2.title"
            width="500"
            >
            <Form :model="modalBox2.entity"  :label-width="80" style="margin-top: 20px">
                
                <FormItem label="接收人" >
                    <Input :value="modalBox2.entity.name" disabled  />
                </FormItem>

                <FormItem label="邮箱地址" >
                    <Input :value="modalBox2.entity.email"  disabled />
                </FormItem>

                
                <FormItem label="发送内容" >
                   <Input v-model="modalBox2.entity.content" type="textarea" :autosize="{minRows: 5}" placeholder="输入发送的内容"></Input>
                </FormItem>

               
                <FormItem >
                    <Button type="primary" style="margin-left:80px" size="large" @click="sendMail">发送</Button>
                   <Button type="default" style="margin-left:25px" size="large" @click="modalBox2.show=false">取消</Button>
                </FormItem>
                
            </Form>
          </Modal>

          

    </div>
</template>
<script>
import userStore from '@/utils/userStore';
import Header from '@/components/Header'
import MD5 from 'md5'
export default {
    name:"index",
    components:{
        Header
    },
    data(){
        return {
            menuActiveName:"",
            menuData:[],
            requestMethods:[],
            requestHeaderNames:[],
            modalBox:{
                show:false,
                title:"修改密码",
                entity:{

                }
            },
             modalBox2:{
                show:false,
                title:"发送邮件",
                entity:{

                }
            },
            adminConfig: {
                edit: false,
                configKey: "adminInfo",
                config: null,
            },
        }
    },
    created(){
         this.menuActiveName = this.$route.path;
         this.storeUserInfo();

        this.loadRequestHeaderAndMethods();

        this.loadConfig(this.adminConfig.configKey,(data)=>{
            this.adminConfig.config = data&&data.configContent?JSON.parse(data.configContent):null;
        });

        this.$on('index:link-manager:click',(config)=>{
            this.modalBox2.entity = config;
            this.modalBox2.show=true;
        })
      
    },
    watch: {

        '$route.path':function(newVal,oldVal){
            this.$nextTick(()=>{
                this.menuActiveName=newVal;
                 this.$refs.menu.updateActiveName();
            })
        },
        '$route.name':function(newVal,oldVal){
            if(newVal === 'api-list' && oldVal === 'projects'){
                this.$bus.$emit('api-list::initDatas');
            }
        }
    },
    methods:{
        saveRePass(){
            var entity = Object.assign({},this.modalBox.entity);
            if(!entity.oldPass||entity.oldPass.length == 0){
                this.$Message.warning("旧密码不能为空");
                 return;
            }

            if(!entity.password||entity.password.length == 0){
                this.$Message.warning("新密码不能为空");
                return;
            }

            if(entity.password !== entity.newPassTow){
                this.$Message.warning("两次输入密码不一致");
                 return;
            }

            entity.password = MD5(entity.password );
            entity.oldPass = MD5(entity.oldPass);
            this.$server.user.currentResetPass(entity).then(res=>{
                var {data} = res;
                if(data.code == 200){
                     this.$Message.success("密码修改成功,稍后请重新登录系统！");
                    setTimeout(() => {
                        this.logout();
                    }, 2000);
                }else{
                  this.$Message.warning(data.msg);

                }
            })
        },
         loadConfig(configKey, callback) {
            this.$server.config.getConfig(configKey).then((res) => {
                const data = res.data;
                if (data.code == 200) {
                    if (callback) {
                        callback(data.data);
                    }
                } else {
                this.$Message.warning(data.msg);
                }
            });
       },
        linkManager(){
            if(!this.adminConfig.config){
                this.$Message.warning("管理员信息还未加载,请稍后")
                return;
            }
            this.$emit('index:link-manager:click',this.adminConfig.config);
        },
        sendMail(){
           var entity = {...this.modalBox2.entity};
           if(!entity||!entity.content||entity.content.length==0){
                this.$Message.warning("发送的内容不能为空")
                return;
           }
          const currentUserName =  this.$store.getters.getLoginUser.user.username;

           const sendEntity={
               subject:"来自API管理平台-"+currentUserName+"的紧急联系",
               to:[entity.email],
               from:`${currentUserName}(API管理平台用户)`,
               content:entity.content,
               isHtml:false
           }

           this.$server.notice.sendMail(sendEntity).then(res=>{
               const {data} = res;
                if (data.code == 200) {
                    console.log(data)
                    this.modalBox2.entity.content="";
                     this.$Message.success("发送成功");
                } else {
                     this.$Message.warning(data.msg);
                }
           })
        },
        // 设置用户信息到Store状态管理中
        storeUserInfo(){
          
            const userInfo = userStore.getUserInfo();
            if(userInfo){
                this.$store.commit('setLoginUser',userInfo);
                return ;
            }

            this.$server.user.getUserInfo().then(res=>{
                var {data} = res;
                if(data.code == 200){
                    userStore.storeUserInfo(data.data);
                    this.$store.commit('setLoginUser',data.data);
                }
            })
          
        },
        // 菜单被选择
        menuSelect(name){
            if(this.$route.path != name){
                this.$router.push({path:name})
            }
        },
        // 下拉菜单被单机
        downMenuClick(name){
            switch(name){
                case "repass":
                    this.repass();
                    break;
                case "logout":
                    this.logout();
                    break;
                default:
            }
           
        },
        // 用户退出
        logout(){
            userStore.cleanStore();
             this.$router.push({
                path:"/login"
            })
        },
        // 修改密码
        repass(){
            this.modalBox.show=true;
        },
        // 点击logo跳转至欢迎页
        toHomePage(){
            if(this.$route.path != '/home'){
                 this.$router.push({path:'/home'})
            }
          
        },
        loadRequestHeaderAndMethods(){
            this.$server.multi(
                this.$server.api.getRequestMethods(),
                this.$server.api.getRequestHeaderNames()
            ).then(res=>{
               var data1 = res[0].data;
               var data2 = res[1].data;
               this.requestMethods=data1.data;
               this.requestHeaderNames=data2.data;
            })
        }
    }
}
</script>

<style lang="scss" scoped>
@import '../../assets/css/animate.min.css';

   .index{
       
      height:inherit;
      .my-menu{
         background-color:  $menu-bg-color;
         color:#B8C7CE;
         .ivu-menu-item-active{
             color:#FFFFFF;
         }
      }
    

      .logo-box{
          float: left;
          width: $def-menu-width;
          background-color:#367FA9;
          padding-left: 50px;
          line-height:50px;
          height: inherit;
          font-size: 20px;
          color:rgb(226, 226, 221);
          font-family: 'Trebuchet MS', 'Lucida Sans Unicode', 'Lucida Grande', 'Lucida Sans', Arial, sans-serif;
          text-shadow: 2px 2px 2px #000;
      }

      .login-user-box{
          float:right;
          height: inherit;
          line-height: 50px;
          padding:0px 25px;
          font-size: 13px;
          color:lavender;

          &:hover{
               cursor: pointer;
               background-color:#367FA9;
          }
      }

      .contact-box{
  
        @extend .login-user-box;
      }
    

      .main-box{
          height: calc(100vh - 50px);
          display: flex;
          flex-direction: row;
          justify-content: flex-start;

          .main-right{
              overflow: hidden;
              position: relative;
              width: calc(100% - #{$def-menu-width}) ;
          }
      }
   }

   .ivu-icon-md-radio-button-off{
       transform: translateY(-2px);
   }
</style>