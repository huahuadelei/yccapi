<template>
  <div class="email-rule" style="min-width: 930px">
    <Card style="font-size: 20px" class="def-box-shadow"> 设置邮箱信息 </Card>
    <div style="padding: 15px">
      <Card class="def-box-shadow">
        <p slot="title">发信服务配置</p>
        <p slot="extra" class="def_btns">
          <span
            class="tools-btn"
            v-if="!emailConfig.edit"
            @click="emailConfig.edit = true"
            >编辑</span
          >
          <span class="tools-btn" v-if="emailConfig.edit" @click="updateEmailConfig">保存</span>
          <span
            class="tools-btn"
            style="margin-left: 15px"
            v-if="emailConfig.edit"
            @click="quitEmailEdit"
            >取消</span
          >
        </p>

        <Row style="line-height: 3; margin-top: 15px; margin-bottom: 15px">
          <Col style="text-align: right" :span="2" offset="1"> 发信协议： </Col>
          <Col :span="5">
            <span v-if="!emailConfig.edit" style="color: green">{{
              emailConfig.config.protocol | EmailValueFilter
            }}</span>
            <Input
              v-if="emailConfig.edit"
              v-model="emailConfig.config.protocol"
            ></Input>
          </Col>
          <Col style="text-align: right" :span="2"> Host： </Col>
          <Col :span="5">
            <span v-if="!emailConfig.edit" style="color: green">{{
              emailConfig.config.host | EmailValueFilter
            }}</span>
            <Input
              v-if="emailConfig.edit"
              v-model="emailConfig.config.host"
            ></Input>
          </Col>
          <Col style="text-align: right" :span="2"> Port： </Col>
          <Col :span="5">
            <span v-if="!emailConfig.edit" style="color: green">{{
              emailConfig.config.port | EmailValueFilter
            }}</span>
            <Input
              v-if="emailConfig.edit"
              v-model="emailConfig.config.port"
            ></Input>
          </Col>
        </Row>
        <Row style="line-height: 3; margin-top: 15px; margin-bottom: 15px">
          <Col style="text-align: right" :span="2" offset="1"> 用户名： </Col>
          <Col :span="5">
            <span v-if="!emailConfig.edit" style="color: green">{{
              emailConfig.config.username | EmailValueFilter
            }}</span>
            <Input
              v-if="emailConfig.edit"
              v-model="emailConfig.config.username"
            ></Input>
          </Col>

          <Col style="text-align: right" :span="2"> 密码： </Col>
          <Col :span="5">
            <span v-if="!emailConfig.edit" style="color: green">{{
              emailConfig.config.password | EmailValueFilter
            }}</span>
            <Input
              v-if="emailConfig.edit"
              v-model="emailConfig.config.password"
            ></Input>
          </Col>
          <Col style="text-align: right" :span="2"> 启用SSL： </Col>
          <Col :span="5">
            <Checkbox
              v-if="emailConfig.edit"
              size="large"
              style="margin-top: -5px"
              v-model="emailConfig.config.useSsl"
            ></Checkbox>
            <span v-if="!emailConfig.edit" style="color: green">{{
              emailConfig.config.useSsl ? "开启" : "关闭"
            }}</span>
          </Col>
        </Row>
      </Card>
      <Card class="def-box-shadow" style="margin-top: 25px">
        <p slot="title">管理员信息</p>
        <p slot="extra" class="def_btns">
          <span
            class="tools-btn"
            v-if="!adminConfig.edit"
            @click="adminConfig.edit = true"
            >编辑</span
          >
          <span class="tools-btn" v-if="adminConfig.edit" @click="updateAdminInfoConfig">保存</span>
          <span
            class="tools-btn"
            style="margin-left: 15px"
            v-if="adminConfig.edit"
            @click="quitAdminInfoEdit"
            >取消</span
          >
        </p>

        <Row style="line-height: 3; margin-top: 15px; margin-bottom: 15px">
          <Col style="text-align: right" :span="2" offset="1"> 管理员名： </Col>
          <Col :span="5">
            <span v-if="!adminConfig.edit" style="color: green">{{
              adminConfig.config.name | AdminValueFilter
            }}</span>
            <Input
              v-if="adminConfig.edit"
              v-model="adminConfig.config.name"
            ></Input>
          </Col>
          <Col style="text-align: right" :span="2"> 邮箱地址： </Col>
          <Col :span="5">
            <span v-if="!adminConfig.edit" style="color: green">{{
              adminConfig.config.email | AdminValueFilter
            }}</span>
            <Input
              v-if="adminConfig.edit"
              v-model="adminConfig.config.email"
            ></Input>
          </Col>
          <Col style="text-align: right" :span="2"> 电话： </Col>
          <Col :span="5">
            <span v-if="!adminConfig.edit" style="color: green">{{
              adminConfig.config.phone | AdminValueFilter
            }}</span>
            <Input
              v-if="adminConfig.edit"
              v-model="adminConfig.config.phone"
            ></Input>
          </Col>
        </Row>
      </Card>
    </div>
  </div>
</template>

<script>
export default {
  name: "email-rule",
  data() {
    return {
      emailConfig: {
        edit: false,
        configKey: "emailSendRule",
        config: {},
      },
      adminConfig: {
        edit: false,
        configKey: "adminInfo",
        config: {},
      },
    };
  },
  created() {
   
      this.loadEmailConfig();
      this.loadAdminInfoConfig();
    
  },
  methods: {
    loadEmailConfig(callback){
        this.loadConfig(this.emailConfig.configKey, (config) => {
            if (config&&config.configContent) {
                this.emailConfig.config=JSON.parse(config.configContent); 
                 if(callback){callback(config)}
            }  
       });
    },  
    loadAdminInfoConfig(callback){
       this.loadConfig(this.adminConfig.configKey, (config) => {
            if (config&&config.configContent) {
                this.adminConfig.config=JSON.parse(config.configContent); 
                  if(callback){callback(config)}
            } 
        });
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
    updateEmailConfig() {
        const emailConfigEntity={
            configKey:this.emailConfig.configKey,
            configContent:JSON.stringify(this.emailConfig.config),
            configType:2,
            configDesc:"发信服务邮箱配置"
        }

         this.$server.config.updateConfig(emailConfigEntity).then(res=>{
             var {data} = res;

             if(data.code == 200){
                this.emailConfig.edit=false;
                this.$Message.success("更新成功");
             }else{
                 this.$Message.warning(data.msg);
             }
         })

    },
    updateAdminInfoConfig() {
          const adminConfigEntity={
            configKey:this.adminConfig.configKey,
            configContent:JSON.stringify(this.adminConfig.config),
            configType:2,
            configDesc:"管理员信息"
        }

         this.$server.config.updateConfig(adminConfigEntity).then(res=>{
             var {data} = res;

             if(data.code == 200){
                this.adminConfig.edit=false;
                this.$Message.success(data.msg);
             }else{
                 this.$Message.warning(data.msg);
             }
         })
    },
    quitEmailEdit(){

        this.loadEmailConfig((conf=>{
                this.emailConfig.edit=false;
        }));

    },
     quitAdminInfoEdit(){

        this.loadAdminInfoConfig((conf=>{
                this.adminConfig.edit=false;
        }));

    }
  },
  filters: {
    EmailValueFilter(val) {
      if (val) {
        return val;
      }
      return "-";
    },
    AdminValueFilter(val) {
      if (val) {
        return val;
      }
      return "-";
    },
  },
};
</script>

<style lang="scss" scoped>
.email-rule {
  height: calc(100vh - 50px);
  overflow-y: auto;
  background-color: #f5f7f9;
  .tools-btn {
    -moz-user-select: none;
    user-select: none;
    display: inline-block;
    color: rgb(32, 170, 180);
    cursor: pointer;
    transition: color 0.5 ease-in-out;
    &:hover {
      transition: color 0.5 ease-in-out;
      transform: scale(1.1);
      color: rgb(50, 121, 252);
    }
  }
}
</style>