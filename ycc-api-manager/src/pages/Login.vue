<template>
  <div class="login">
    <div class="login-box">
      <div class="">
        <h3 class="text-not-select">YCC接口文档管理平台只为便利而生</h3>
      </div>
      <Form
        ref="login-form"
        style="margin-top: 50px"
        :model="loginEntity"
        :rules="ruleValidate"
      >
        <FormItem prop="username">
          <Input
            prefix="ios-contact"
            size="large"
            style="width: 100%"
            v-model="loginEntity.username"
            placeholder="请输入帐号"
          />
        </FormItem>

        <FormItem prop="password">
          <Input
            prefix="md-lock"
            size="large"
            type="password"
            style="width: 100%"
            v-model="loginEntity.password"
            placeholder="请输入密码"
          />
        </FormItem>
        <FormItem style="text-align: left">
          <Checkbox size="large" class="text-not-select" v-model="autoLogin"
            >下次自动登录</Checkbox
          >

          <a href="javaScript:" style="float: right; font-size: 14px"
            >忘记密码?</a
          >
        </FormItem>
        <FormItem>
          <Button type="primary" long size="large" :loading="sendLoginReq" @click="submitd">登录</Button>
        </FormItem>
      </Form>
    </div>
  </div>
</template>
<script>
import MD5 from 'md5'
import userStore from '@/utils/userStore';
export default {
  name: "login",
  data() {
    return {
      sendLoginReq:false,
      autoLogin: false,
      loginEntity: {},
      ruleValidate: {
        username: { required: true, message: "登录名必须填写" },
        password: { required: true, message: "密码必须填写" },
      },
    };
  },
  props:{
    redirect:{
      type:Boolean,
      required:false,
      default:()=>false
    }
  },
  mounted() {
    if(this.redirect){
        var closeCN =  this.$Message.warning({
            content: "您还未登录或身份过期",
              duration: 3,
          });
    }
  },
  methods: {
    loadUserInfo(){
      this.$server.user.getUserInfo().then(res=>{
        var {data} = res;
          if(data.code == 200){
            userStore.storeUserInfo(data.data);
          }
       

      
      })
     
    },
    login() {
        
       this.sendLoginReq=true;
       // 密码加密
       const entity = {...this.loginEntity};
       entity.password = MD5(entity.password); 

       this.$server.user.login(entity).then((res) => {
        var {data} = res;

           
        if (data.code == 200) {
             let {token} = data.data;

             userStore.storeToken(token,this.autoLogin);
               var closeCN =  this.$Message.success({
                  content: "登陆成功",
                    duration: 3,
                });
             this.loadUserInfo();
             setTimeout(()=>{
                this.$router.push({ name: "home" });
             },3000)
         
        } else {
          this.sendLoginReq=false;
          this.$Message.warning({
            content: data.msg,
            duration: 3,
          });
        }
      });
    },
    submitd() {
      var $t = this;
      this.$refs["login-form"].validate((valid) => {
        if (valid) {
          $t.login();
        } else {
          _this.$Message.warning({
            content: "请先补充完整相关数据在重试",
            duration: 3,
          });
        }
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.login {
  &::after{
    content:'';
    position: absolute;
    left: -50px;
    right: -50px;
    top: -50px;
    bottom: -50px;
    background-image: url('../assets/img/bg01.jpg');
    background-size: 100%;
    filter: blur(20px);
  }
  overflow: hidden;
  background-size: 150%;
  background-position: 50%;
  width: inherit;
  height: inherit;
  min-width: 700px;
  position: relative;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;


  .login-box {
    position: relative;
    z-index: 1;
    width: 400px;
    text-align: center;
    height: 400px;
    padding: 50px 40px;
    box-shadow: 0 0 3px rgba(0,0,0,.2);
    border-radius: 8px;
    transform: translateY(-60px);
    overflow: hidden;
    

    &::after{
      content: "";
      position: absolute;
      left: -30px;
      right: -30px;
      top: -30px;
      bottom:-30px;
      background-color: rgba($color: #ffffff, $alpha:.6);
      border-radius: 8px;
      z-index: -1;
      filter: blur(50px);

    }
  }
}




</style>