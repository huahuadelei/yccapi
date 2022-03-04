<template>
  <div class="menu-permission">
    <Card style="width: 100%" class="def-box-shadow">
      <Button slot="title" type="primary" @click="addMenu">
        <Icon type="md-add" />
        添加
      </Button>

      <Table
        row-key="menuId"
        :loading="tableModel.loading"
        :columns="tableModel.columns"
        :data="tableModel.columnDatas"
        border
      ></Table>
    </Card>

    <Modal
      :footer-hide="true"
      :mask-closable="false"
      v-model="modelBox.show"
      :title="modelBox.title"
      width="750"
    >
      <Form :model="menuEntity" :label-width="80" style="margin-top: 20px">
        <FormItem label="类型">
          <Row>
            <Col span="11">
              <RadioGroup v-model="menuEntity.isMenu">
                <Radio :label="1" border>菜单</Radio>
                <Radio :label="0" border>权限</Radio>
              </RadioGroup>
            </Col>
            <Col span="3"> 上级菜单 </Col>
            <Col span="10">
              <Treeselect
                v-model="menuEntity.parentId"
                :options="TreeselectModel.options"
              />
            </Col>
          </Row>
        </FormItem>

        <FormItem label="主题色" v-if="menuEntity.isMenu == 1">
          <ColorPicker v-model="menuEntity.menuTheme" />
        </FormItem>

        <FormItem label="菜单名称">
          <Input v-model="menuEntity.menuName" placeholder="请输入菜单名称" />
        </FormItem>

        <FormItem label="路由地址" v-if="menuEntity.isMenu == 1">
          <Input v-model="menuEntity.routePath" placeholder="请输入路由地址" />
        </FormItem>

        <FormItem label="权限标识" v-if="menuEntity.isMenu == 0">
          <Input v-model="menuEntity.permCode" placeholder="请输入权限标识" />
        </FormItem>

        <FormItem label="排序" style="margin-bottom: 70px">
          <InputNumber
            :min="1"
            :step="1"
            v-model="menuEntity.sort"
          ></InputNumber>
        </FormItem>
        <div class="modal_tool_btn">
          <Button type="default" size="large" @click="modelBox.show = false"
            >取消</Button
          >
          <Button type="primary" size="large" @click="saveMenu">保存</Button>
        </div>
      </Form>
    </Modal>
  </div>
</template>

<script>
// import the component
import Treeselect from "@riophae/vue-treeselect";
// import the styles
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
export default {
  name: "menu-permission",
  components: {
    Treeselect,
  },
  data() {
    return {
      TreeselectModel: {
        options: [],
      },
      tableModel: {
        loading: false,
        columns: [
          {
            title: "菜单名称",
            key: "menuName",
            tree: true,
          },
          {
            title: "主题",
            width: 120,
            align: "center",
            key: "menuTheme",
            render: (h, params) => {
              if (!params.row["menuTheme"]) {
                return;
              }
              return h("Icon", {
                class: {
                  "text-weight": true,
                },
                props: {
                  type: "md-radio-button-off",
                  color: params.row["menuTheme"],
                },
              });
            },
          },
          {
            title: "排序",
            key: "sort",
            align: "center",
          },
          {
            title: "路由地址",
            key: "routePath",
            align: "center",
          },
          {
            title: "类型",
            key: "isMenu",
            align: "center",
            render: (h, params) => {
              return h(
                "Tag",
                {
                  props: {
                    type: "dot",
                    color: params.row["isMenu"] ? "warning" : "primary",
                  },
                },
                params.row["isMenu"] ? "菜单" : "权限"
              );
            },
          },
          {
            title: "权限标识",
            key: "permCode",
            align: "center",
          },
          {
            title: "操作",
            key: "action",
            align: "center",
            width: 210,
            render: (h, params) => {
              return h("div", [
                h(
                  "span",
                  {
                    class: { "def-btn-group": true },
                    on: {
                      click: () => {
                        this.addMenuItemClick(params.row);
                      },
                    },
                  },
                  [
                    h("Icon", {
                      props: {
                        type: "md-add",
                      },
                    }),
                    "添加",
                  ]
                ),
                h(
                  "span",
                  {
                    class: { "def-btn-group": true },
                    on: {
                      click: () => {
                        this.updateMenuItemClick(params.row);
                      },
                    },
                  },
                  [
                    h("Icon", {
                      props: {
                        type: "ios-create",
                      },
                    }),
                    "修改",
                  ]
                ),

                h(
                  "span",
                  {
                    class: { "def-btn-group": true },
                    on: {
                      click: () => {
                        this.delMenuItemClick(params.row);
                      },
                    },
                  },
                  [
                    h("Icon", {
                      props: {
                        type: "md-remove",
                      },
                    }),
                    "删除",
                  ]
                ),
              ]);
            },
          },
        ],
        columnDatas: [],
      },
      menuEntity: {
        menuTheme: "#00C0EF",
      },
      modelBox: {
        show: false,
        title: "添加菜单",
      },
    };
  },
  methods: {
    saveMenu() {
      if (!this.menuEntity.menuName) {
        this.$Message.warning({
          content: "菜单名称必须填写",
          duration: 2,
        });
        return;
      }

      this.$server.menu.saveMenu(this.menuEntity).then((res) => {
        const { data } = res;
        if (data.code == 200) {
          this.$Message.success({
            content: "添加菜单成功",
            duration: 2,
          });

          this.loadMenuData();
          this.modelBox.show = false;
        } else {
          this.$Message.warning({
            content: data.msg,
            duration: 2,
          });
        }
      });
    },
    addMenuItemClick(row) {
      this.menuEntity = {
        parentId: row.menuId,
        menuTheme: "#00C0EF",
        isMenu: 1,
        sort: 1,
      };
      this.modelBox = {
        show: true,
        title: "添加菜单",
      };
    },
    updateMenuItemClick(row) {
      const newObje = Object.assign({}, row, {
        id: row.menuId,
        isMenu: row.isMenu ? 1 : 0,
      });

      this.menuEntity = newObje;
      this.modelBox = {
        show: true,
        title: "修改菜单",
      };
    },
    delMenuItemClick(row) {
      this.$Modal.confirm({
        title: "删除",
        content:
          `<p>是否删除[${row.menuName}]这个` +
          (row.isMenu ? "菜单" : "权限") +
          `?</p>`,
        loading: true,
        onOk: () => {
          this.$server.menu.removeMenu(row.menuId).then((res) => {
            const data = res.data;

            if (data.code == 200) {
              this.loadMenuData();
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
    loadMenuData() {
      this.tableModel.loading = true;
      this.$server.menu.getTree().then((res) => {
        var { data } = res;

        if (data.code == 200) {
          this.tableModel.columnDatas = data.data;

          this.tableModel.loading = false;
          const menuTreeFormatPostDatas = this.initTreeselectData(data.data);
          this.TreeselectModel.options = [
            {
              id: "0",
              label: "根节点",
              children: menuTreeFormatPostDatas,
            },
          ];
        } else {
          this.$Message.warning({
            content: data.msg,
            duration: 2,
          });
        }
      });
    },
    // 初始化属性选择组件的数据
    initTreeselectData(data) {
      if (!data || data.length == 0) {
        return null;
      }

      const resultArray = [];

      for (var i = 0; i < data.length; i++) {
        let item = data[i];

        const newObj = {};
        newObj.id = item.menuId;
        newObj.label = item.menuName;
        let child = this.initTreeselectData(item.children);
        if (child) {
          newObj.children = child;
        }
        resultArray.push(newObj);
      }
      return resultArray;
    },
    addMenu() {
      this.menuEntity = {
        isMenu: 1,
        menuTheme: "#00C0EF",
        parentId: "0",
        sort: 1,
      };
      this.modelBox = {
        show: true,
        title: "添加菜单",
      };
    },
  },
  created() {
    this.loadMenuData();
  },
};
</script>

<style lang="scss"coped>
.menu-permission {
  height: 100%;
  position: relative;
  padding: 15px;
  overflow-y: auto;
  .ivu-card {
    height: inherit;
    overflow-x: hidden;
    overflow-y: auto;
  }

  // 按钮组样式
  .def-btn-group {
    font-size: 13px;
    margin-left: 12px;
    color: $header-bg-color;
    cursor: pointer;
    &:hover {
      text-shadow: 0 0 5px rgba($color: $header-bg-color, $alpha: 0.5);
    }
  }
}
.ivu-table-overflowX {
  overflow-x: hidden !important;
}
.ivu-card-head {
  padding: 10px !important;
}
.ivu-modal-body {
  position: relative;
  .modal_tool_btn {
    left: 0;
    right: 0;
    padding-right: 30px;
    position: absolute;
    bottom: 25px;
    & button {
      float: right;
      & + button {
        margin-right: 20px;
      }
    }
  }
}
</style>