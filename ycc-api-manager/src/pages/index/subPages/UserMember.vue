<template>
  <div class="user-member">
    <Card class="def-box-shadow" style="height: 72px; margin-bottom: 15px">
        <Col :span="3">
            <Button icon="md-add" type="primary" size="large" @click="addUser">
                添加
            </Button>
        </Col>
        <Col :span="6" offset="15">
            <Input size="large"
            @on-search="()=>{queryEntity.pageNum=1;loadUserList();}"
             search 
             enter-button
              placeholder="请输入用户名称进行搜索" 
              v-model="queryEntity.likeBox.likeValue"/>
        </Col>
    </Card>
    <Card class="def-box-shadow auto_height">
      <Table
        row-key="id"
        :loading="tableModel.loading"
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
      v-model="modalBox.show"
      :title="modalBox.title"
      width="500"
    >
      <Form :model="userEntity" :label-width="80" style="margin-top: 20px">
        <FormItem label="用户名">
          <Input
            v-model="userEntity.username"
            :disabled="userEntity.id != null && userEntity != undefined"
            placeholder="请输入用户名"
          />
        </FormItem>

        <FormItem label="角色名称">
          <Select
            v-model="userEntity.roleId"
            filterable
            :disabled="userEntity.username && userEntity.username === 'admin'"
          >
            <Option v-for="item in roleList" :value="item.id" :key="item.id">{{
              item.roleName
            }}</Option>
          </Select>
        </FormItem>

        <FormItem label="邮箱地址">
          <Input
            type="email"
            v-model="userEntity.email"
            placeholder="请输入邮箱"
          />
        </FormItem>

        <FormItem label="密码" v-if="!userEntity.id">
          <Input
            type="password"
            v-model="userEntity.password"
            placeholder="请输入密码"
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
import MD5 from 'md5'
export default {
  name: "user-member",
  components: {},
  data() {
    return {
      queryEntity: {
        total: 0,
        pageNum: 1,
        pageSize: 10,
        likeBox:{
          likeValue:"",
          column:"username"
        }
      },
      tableModel: {
        loading: true,
        columns: [
          {
            title: "ID",
            key: "id",
          },
          {
            title: "用户名",
            key: "username",
          },
          {
            title: "角色",
            render: (h, params) => {
              return h("Tag", this.getRoleObjById(params.row.roleId).roleName);
            },
          },
          {
            title: "邮箱地址",
            key: "email",
          },
          {
            title: "最后更新时间",
            key: "updateTime",
          },
          {
            title: "操作",
            width: 220,
            render: (h, params) => {
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
                        this.updateUser(params.row);
                      },
                    },
                  },
                  "修改"
                )
              );

              if (
                this.getRoleObjById(params.row.roleId).roleCode !==
                "Administrator"
              ) {
                btn_group_array.push(
                  h(
                    "Button",
                    {
                      style: {
                        "margin-left": "10px",
                      },
                      props: {
                        type: "info",
                        size: "small",
                        ghost: true,
                      },
                      on: {
                        click: () => {
                          this.resetPass(params.row);
                        },
                      },
                    },
                    "重置密码"
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
                          this.deleteUser(params.row);
                        },
                      },
                    },
                    "删除"
                  )
                );
              }

              return h("div", btn_group_array);
            },
          },
        ],
        columnDatas: [],
      },
      roleList: [],
      userEntity: {},
      modalBox: {
        show: false,
        title: "添加用户",
      },
      newPass:""
    };
  },
  created() {
    this.loadRoleList(() => {
      this.loadUserList();
    });
  },
  methods: {
    changePage(currentPage){
      this.queryEntity.pageNum = currentPage;
      this.loadUserList();

    },
    changePageSize(newPageSize){
      this.queryEntity.pageSize = newPageSize;
      this.loadUserList();
    },
    /**
     * 加载角色列表
     */
    loadRoleList(callback) {
      this.$server.role.getRoleList().then((res) => {
        var data = res.data;
        if (data.code == 200) {
          this.roleList = data.data;
          if (callback) {
            callback();
          }
        } else {
          this.loadRoleList();
        }
      });
    },
    /**
     * 加载用户列表
     */
    loadUserList() {
      this.tableModel.loading = true;
      this.$server.user.queryUserList(this.queryEntity).then((res) => {
        var { data } = res;

        this.tableModel.loading = false;
        if (data.code == 200) {
          this.tableModel.columnDatas = data.data.records;
          this.queryEntity.total = data.data.total;
        } else {
          this.$Message.warning(data.msg);
        }
      });
    },
    /**
     * 通过角色id获取角色名称
     */
    getRoleObjById(roleId) {
      for (let i = 0; i < this.roleList.length; i++) {
        const item = this.roleList[i];
        if (item.id == roleId) {
          return item;
        }
      }
      return "未知角色";
    },
    // 修改
    updateUser(row) {
      this.userEntity = Object.assign({}, row);
      this.modalBox = {
        show: true,
        title: "修改用户",
      };
    },

    // 删除用户
    deleteUser(row) {
        this.$Modal.confirm({
                title: "删除",
                content:
                `<p>是否删除[${row.username}]这个用户?</p>`,
                loading: true,
                onOk: () => {
                this.$server.user.deleteUser(row.id).then((res) => {
                    const data = res.data;
                   this.$Modal.remove();

                    if (data.code == 200) {

                      this.loadUserList();
                      this.$Message.info('删除成功')

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

    // 添加用户
    addUser() {
      this.userEntity = {};
      this.modalBox = {
        show: true,
        title: "添加用户",
      };
    },
    // 保存用户数据
    saveData() {
      const entity = this.userEntity;
      if (!entity.username || entity.username.trim() == "") {
        this.$Message.warning("用户名不能为空");
        return;
      }

      if (!entity.roleId) {
        this.$Message.warning("角色不能为空");
        return;
      }

      if (!entity.id) {
        // id为空 新增用户

        if (!entity.password || entity.password.trim() == "") {
          this.$Message.warning("密码不能为空");
          return;
        }

        entity.password=MD5(entity.password)

        this.$server.user.createUser(entity).then((res) => {
          const { data } = res;
          if (data.code == 200) {
            this.modalBox.show = false;
            this.loadUserList();
            this.$Message.success("修改成功");
          } else {
            this.$Message.warning(data.msg);
          }
        });
      } else {
        // id不为空 修改用户
        this.$server.user.updateUser(entity).then((res) => {
          const { data } = res;

          if (data.code == 200) {
            this.modalBox.show = false;
            this.loadUserList();
            this.$Message.success("添加成功");
          } else {
            this.$Message.warning(data.msg);
          }
        });
      }
    },
    // 重置密码
    resetPass(row) {
      this.newPass = "";
      this.$Modal.confirm({
        title: "重置密码",
        loading:true,
        render: (h) => {
          const elements = [];
          elements.push(
            h("p", [
              `将要重置`,
              h("b", { style: { color: "red" } }, `${row.username}`),
              "的密码,请输入新密码",
            ])
          );

          elements.push(
            h("p",{
                style:{
                    'margin-top':'15px'
                }
            }, [
                h('Input',{
                    props:{
                        value:this.newPass,
                        placeholder:"请输入新密码"
                    },
                    on:{
                        input:(val)=>{
                            this.newPass = val;
                        }
                    }
                })
            ])
          );


          return h("div", elements);
        },
        onOk:()=>{


            const updateData = {
                id:row.id,
                password:MD5(this.newPass)
            }

            this.$server.user.resetPass(updateData).then(res=>{
                const data = res.data;
                    this.$Modal.remove();

                 if (data.code == 200) {
                    this.$Message.success("重置密码成功");
                } else {
                    this.$Message.warning(data.msg);
                }
                
            })
      
             
        }
      });

    },
  },
};
</script>

<style lang="scss" scoped>
.user-member {
  height: calc(100vh - 50px);
  overflow-y: auto;
  position: relative;
  padding: 15px;
}

.auto_height {
  height: calc(100% - 85px);
  min-height: 500px;
  overflow-y:auto;
  overflow-x:hidden;

  & > * {
    height: inherit;
  }

}
</style>