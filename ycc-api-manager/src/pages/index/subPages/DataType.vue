<template>
  <div class="data-type">
    <Card class="def-box-shadow" style="height: 65px">
      <Col :span="2">
        <Button
          icon="md-add"
          type="primary"
          @click="
            tableModel.columnDatas.push({
              _editrow: true,
              typeName: '',
              typeDesc: '',
            })
          "
        >
          添加
        </Button>
      </Col>
       <Col :span="10" offset="1">
           <Alert>双击单元格 <strong>数据类型</strong>&nbsp;或&nbsp; <strong>描述</strong> 可实现行内编辑</Alert>
       </Col>
    </Card>

    <div class="content">
      <Card class="def-box-shadow">
        <Table
          row-key="id"
          :loading="tableModel.loading"
          :columns="tableModel.columns"
          :data="tableModel.columnDatas"
          border
        ></Table>
      </Card>
    </div>
  </div>
</template>

<script>
export default {
  name: "data-type",
  data() {
    return {
      tableModel: {
        loading: true,
        columns: [
          {
            title: "RorNumber",
            render: (h, { row, index }) => {
              return h("span", index+1);
            },
          },
          {
            title: "数据类型",
            key: "typeName",
            render: (h, { row, index }) => {
              var element = !row._editrow
                ? row.typeName
                : h("Input", {
                    props: {
                      value: row.typeName,
                      placeholder:"请输入数据类型"
                    },
                    on: {
                      input: (val) => {
                        this.tableModel.columnDatas[index].typeName = val;
                      },
                    },
                  });
              return h(
                "div",
                {
                  on: {
                    dblclick: () => {
                          if(!this.tableModel.columnDatas[index].__originData){
                            this.tableModel.columnDatas[index].__originData=row;
                        }
                      this.tableModel.columnDatas[index]._editrow = true;
                    },
                  },
                },
                [element]
              );
            },
          },
          {
            title: "描述",
            key: "typeDesc",
            render: (h, { row, index }) => {
              var element = !row._editrow
                ? row.typeDesc
                : h("Input", {
                    props: {
                      value: row.typeDesc,
                      placeholder:"请输入描述信息"
                    },
                    on: {
                      input: (val) => {
                        this.tableModel.columnDatas[index].typeDesc = val;
                      },
                    },
                  });
              return h(
                "div",
                {
                  on: {
                    dblclick: () => {
                        if(!this.tableModel.columnDatas[index].__originData){
                            this.tableModel.columnDatas[index].__originData=row;
                        }
                      this.tableModel.columnDatas[index]._editrow = true;
                    },
                  },
                },
                [element]
              );
            },
          },
          {
            title: "创建时间",
            key: "createTime",
            render: (h, { row, index }) => {
              var text = row._editrow && !row.createTime ? "-" : row.createTime;
              return h("span", text);
            },
          },
          {
            title: "操作",
            width: "160",
            render: (h, params) => {
              var btnTools = [];

              if (params.row._editrow) {
                btnTools.push(
                  h(
                    "Button",
                    {
                      on: {
                        click: () => {
                          this.saveRowData(params.row, params.index);
                        },
                      },
                    },
                    "保存"
                  )
                );
              }

              btnTools.push(
                h(
                  "Button",
                  {
                    style: {
                      "margin-left": "2px",
                    },
                    on: {
                      click: () => {
                        this.deleteRowData(params.row, params.index);
                      },
                    },
                  },
                  "删除"
                )
              );

              return h("div", btnTools);
            },
          },
        ],
        columnDatas: [],
      },
    };
  },
  created() {
    this.loadDataTypeList();
  },
  methods: {
    equalsObj(o1, o2) {
      if (o1 == null || o2 == null) {
        return false;
      } else if (o1 === o2) {
        return true;
      }else{
          for (var key in o1) {
              if(key.indexOf('_')==0){
                  continue;
              }
              if (o1[key] !== o2[key]) {
                return false;                  
              }
          }
        return true;
      }
    },
    saveRowData(row,index){
        if(this.equalsObj(this.tableModel.columnDatas[index],this.tableModel.columnDatas[index].__originData)){
            this.tableModel.columnDatas[index]._editrow=false;
            return;
        }

        if(!row.typeName||row.typeName == ''){
            this.$Message.warning('数据类型不能为空');
            return;
        }

        this.$server.dataType.updateDataType(row).then(res=>{
            const {data} = res;
            if (data.code == 200) {
                this.loadDataTypeList();
                this.$Message.info('保存成功')
            } else {
                this.$Message.warning(data.msg);
             }
        })
    },
    loadDataTypeList() {
      this.loading = true;
      this.$server.dataType.getDataTypeList().then((res) => {
        const { data } = res;
        if (data.code == 200) {
          data.data.forEach((arr) => {
            arr._editrow = false;
          });
          this.tableModel.columnDatas = data.data;
          this.tableModel.loading = false;
        } else {
          this.$Message.warning(data.msg);
        }
      });
    },
    deleteRowData(row, index) {
        if (!row.id) {
            this.tableModel.columnDatas.splice(index, 1);
            return ;
        } 

         this.$Modal.confirm({
        title: "删除",
        content:
          `<p>是否删除[${row.typeName}]这个类型?</p>`,
        loading: true,
        onOk: () => {
             this.$server.dataType.deleteDataType(row.id).then(res=>{
                var data = res.data;
                if (data.code == 200) {
                    this.$Modal.remove();
                    this.loadDataTypeList();
                    this.$Message.info('删除成功')
                } else {
                    this.$Message.warning(data.msg);
                }
           })
        },
      });
        
    },
  },
};
</script>

<style lang="scss" scoped>
.data-type {
  height: calc(100vh - 50px);
  overflow-y: auto;
  width: 100%;
  .content {
    width: inherit;
    height: auto;
    padding: 10px;
  }
}
</style>