<template>
    <div class="api-list">
        <Card style="height: 40px; line-height: 10px" class="def-box-shadow">
            <Breadcrumb separator=">">
                <BreadcrumbItem to="/projects">项目管理</BreadcrumbItem>
                <BreadcrumbItem>接口管理</BreadcrumbItem>
            </Breadcrumb>
        </Card>
        <div class="content-main">
            <Row>
                <Col span="5">
                    <a class="projectBlockBtn" href="javaScript:"> 电子签约 </a>
                    <MyList
                            style="margin-top: 15px"
                            title="版本分支"
                            @on-item-click="versionItemClick"
                            :item-tools-render="versionItemToolsRender"
                            :itemDatas="versionItemDatas"
                            v-model="versionActiveValue"
                    >
                        <template slot="extra">
              <span
                      @click="
                  versionEdit = {
                    boxShow: true,
                    entity: {},
                    isCopy: false,
                    loading: false,
                  }
                "
              >
                <Icon
                        class="list-hover"
                        type="md-add"
                        size="20"
                        style="
                    font-weight: 700;
                    margin-top: 5px;
                    margin-right: 5px;
                    cursor: pointer;
                  "
                />
              </span>
                        </template>
                    </MyList>

                    <MyList
                            style="margin-top: 15px"
                            title="接口分组"
                            @on-item-click="groupItemClick"
                            :itemDatas="groupItemDatas"
                            :item-tools-render="groupItemToolsRender"
                            v-model="groupActiveValue"
                    >
            <span
                    slot="extra"
                    @click="groupEdit = { boxShow: true, entity: {}, loading: false }"
            >
              <Icon
                      class="list-hover"
                      type="md-add"
                      size="20"
                      style="
                  font-weight: 700;
                  margin-top: 5px;
                  margin-right: 5px;
                  cursor: pointer;
                "
              />
            </span>
                    </MyList>
                </Col>
                <Col span="19" style="padding-left: 25px">
                    <MyTable :columns="columns" :columnDatas="columnDatas" iviewTable>
                        <template #header-left>
                            <Button
                                    icon="md-add"
                                    type="info"
                                    size="small"
                                    class="add-btn"
                                    @click="
                  $router.push({
                    name: 'api-edit',
                    query: { proVersionId: versionActiveValue },
                  })
                "
                            >添加接口
                            </Button
                            >
                        </template>
                        <template #header-right>
                            <Input
                                    search
                                    size="small"
                                    v-model="apiQueryEntity.likeBox.likeValue"
                                    @on-search="loadApiInfoList"
                                    placeholder="接口名称"
                            />
                        </template>
                        <template #floor>
                            <div class="floor-content" style="width: 100%; text-align: right">
                                <Page
                                        :current="apiQueryEntity.pageNum"
                                        :total="apiQueryEntity.total"
                                        :page-size="15"
                                        size="small"
                                        show-total
                                        @on-change="
                    (numPage) => {
                      apiQueryEntity.pageNum = numPage;
                      loadApiInfoList();
                    }
                  "
                                />
                            </div>
                        </template>
                    </MyTable>
                </Col>
            </Row>
        </div>

        <Modal
                :footer-hide="true"
                :mask-closable="false"
                v-model="versionEdit.boxShow"
                title="添加版本分支"
                width="500"
        >
            <Form
                    :model="versionEdit.entity"
                    :label-width="80"
                    style="margin-top: 20px"
            >
                <FormItem label="版本名称">
                    <Input
                            v-model="versionEdit.entity.versionInfo"
                            placeholder="请输入版本名称"
                    />
                </FormItem>

                <FormItem>
                    <Checkbox v-model="versionEdit.isCopy">从现有版本分支中拷贝</Checkbox>
                </FormItem>

                <FormItem label="目标版本" v-if="versionEdit.isCopy">
                    <Select v-model="versionEdit.entity.copyVersionId">
                        <Option
                                v-for="item in versionItemDatas"
                                :value="item.value"
                                :key="item.value"
                        >{{ item.label }}
                        </Option
                        >
                    </Select>
                </FormItem>

                <FormItem>
                    <Button
                            type="primary"
                            style="margin-left: 80px"
                            size="large"
                            @click="saveVersionData()"
                            :loading="versionEdit.loading"
                    >保存
                    </Button
                    >
                    <Button
                            type="default"
                            style="margin-left: 25px"
                            size="large"
                            @click="versionEdit.boxShow = false"
                    >取消
                    </Button
                    >
                </FormItem>
            </Form>
        </Modal>

        <Modal
                :footer-hide="true"
                :mask-closable="false"
                v-model="groupEdit.boxShow"
                title="添加接口分组"
                width="500"
        >
            <Form
                    :model="groupEdit.entity"
                    :label-width="80"
                    style="margin-top: 20px"
            >
                <FormItem label="分组名称">
                    <Input
                            v-model="groupEdit.entity.groupName"
                            placeholder="请输入分组名称"
                    />
                </FormItem>

                <FormItem>
                    <Button
                            type="primary"
                            style="margin-left: 80px"
                            size="large"
                            @click="saveGroupData()"
                            :loading="groupEdit.loading"
                    >保存
                    </Button
                    >
                    <Button
                            type="default"
                            style="margin-left: 25px"
                            size="large"
                            @click="groupEdit.boxShow = false"
                    >取消
                    </Button
                    >
                </FormItem>
            </Form>
        </Modal>

        <MyLoading :show="loading" type="3" text="数据准备中..."></MyLoading>
    </div>
</template>

<script>
    import MyLoading from "@/components/MyLoading";
    import MyList from "@/components/MyList/MyList";
    import MyListItem from "@/components/MyList/MyListItem";
    import MyTable from "@/components/MyTable/MyTable";

    export default {
        name: "api-list",
        components: {
            MyListItem,
            MyList,
            MyTable,
            MyLoading,
        },
        data() {
            return {
                versionEdit: {
                    boxShow: false,
                    loading: false,
                    entity: {},
                },
                groupEdit: {
                    boxShow: false,
                    loading: false,
                    entity: {},
                },
                apiEntity: {},
                versionActiveValue: "",
                groupActiveValue: "",
                loading: true,
                columns: [
                    {
                        title: "URL",
                        key: "apiUrl",
                        width: "300",
                        render: (h, {row, index}) => {
                            let color = "default";

                            if (row.reqMethod == "POST") {
                                color = "warning";
                            } else if (row.reqMethod == "GET") {
                                color = "primary";
                            } else if (row.reqMethod == "DELETE") {
                                color = "error";
                            } else if (row.reqMethod == "PUT") {
                                color = "geekblue";
                            }

                            return h("span", [
                                h(
                                    "Tag",
                                    {
                                        props: {
                                            color: color,
                                        },
                                    },
                                    row.reqMethod
                                ),
                                row.apiUrl,
                            ]);
                        },
                    },
                    {
                        title: "名称",
                        key: "apiDesc",
                    },
                    {
                        title: "分组",
                        key: "groupId",
                        render: (h, {row, index}) => {
                            var groupName = "默认分组";

                            if (row.groupId !== "0") {
                                if (this.groupItemDatas.length > 0) {
                                    for (let item of this.groupItemDatas) {
                                        if (item.value == row.groupId) {
                                            groupName = item.label;
                                            break;
                                        }
                                    }
                                }
                            }

                            return h("span", groupName);
                        },
                    },
                    {
                        title: "状态",
                        width: "100",
                        render: (h, {row, index}) => {
                            var element;

                            if (row.status === 1) {
                                element = h(
                                    "Tag",
                                    {
                                        props: {
                                            color: "cyan",
                                        },
                                    },
                                    "正常"
                                );
                            } else if (row.status === 2) {
                                element = h("Tag", "维护");
                            } else if (row.status === 3) {
                                element = h("Tag", {props: {color: "red"}}, "废弃");
                            } else {
                                element = h("Tag", row.status);
                            }

                            return element;
                        },
                    },
                    {
                        title: "操作",
                        width: "140",
                        render: (h, {row, index}) => {
                            var btnGroup = [];

                            btnGroup.push(
                                h(
                                    "span",
                                    {
                                        class: {
                                            "my-tag": true,
                                            "my-tag-info": true,
                                        },
                                        on: {
                                            click: () => {
                                                this.$router.push({
                                                    name: "api-edit",
                                                    query: {
                                                        proVersionId: this.versionActiveValue,
                                                        apiInfoId: row.id,
                                                    },
                                                });
                                            },
                                        },
                                    },
                                    "编辑"
                                )
                            );

                            btnGroup.push(
                                h(
                                    "span",
                                    {
                                        class: {
                                            "my-tag": true,
                                            "my-tag-error": true,
                                        },
                                        on: {
                                            click: () => {
                                                this.deleteBtnClick(row);
                                            },
                                        },
                                    },
                                    "删除"
                                )
                            );

                            return h("div", btnGroup);
                        },
                    },
                ],
                columnDatas: [],
                projectId: "",
                projectInfo: {},
                versionItemDatas: [],
                groupItemDatas: [],
                apiQueryEntity: {
                    pageSize: 15,
                    pageNum: 1,
                    total: 0,
                    entity: {
                        projectVersionId: null,
                    },
                    likeBox: {
                        column: "api_url,api_desc",
                    },
                },
            };
        },
        methods: {
            saveGroupData() {
                const reqBody = {
                    projectVersionId: this.versionActiveValue,
                    groupName: this.groupEdit.entity.groupName,
                };

                this.groupEdit.loading = true;

                this.$server.project.addProjectGroup(reqBody).then((res) => {
                    const {data} = res;
                    this.groupEdit.loading = false;
                    if (data.code == 200) {
                        this.groupEdit.boxShow = false;
                        this.$Message.success("添加成功");
                        this.loadApiGroups();
                    } else {
                        this.$Message.warning(data.msg);
                    }
                });
            },
            saveVersionData() {
                const reqBody = {
                    projectId: this.projectId,
                    versionInfo: this.versionEdit.entity.versionInfo,
                };

                if (this.versionEdit.isCopy) {
                    reqBody.copyVersionId = this.versionEdit.entity.copyVersionId;
                }

                this.versionEdit.loading = true;

                this.$server.project.addProjectVersion(reqBody).then((res) => {
                    const {data} = res;
                    this.versionEdit.loading = false;
                    if (data.code == 200) {
                        this.versionEdit.boxShow = false;
                        this.$Message.success("添加成功");
                        this.initProjectAndVersions();
                    } else {
                        this.$Message.warning(data.msg);
                    }
                });
            },
            deleteBtnClick(row) {
                this.$Modal.confirm({
                    title: "删除",
                    content: `<p>是否删除接口 '${row.reqMethod + ":" + row.apiUrl}'?</p>`,
                    loading: true,
                    onOk: () => {
                        this.$server.api.deleteApiInfo(row.id).then((res) => {
                            const data = res.data;
                            this.$Modal.remove();

                            if (data.code == 200) {
                                this.loadApiInfoList();
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
            storeGroup() {
                this.$bus.storage.session.setItem("groupItemDatas", this.groupItemDatas);
            },
            versionItemClick(item) {
                this.apiQueryEntity.entity.projectVersionId = item.value;
                this.loadApiGroups();
            },
            groupItemClick(item) {
                console.log(item)
                this.apiQueryEntity.entity.groupId = item.value;

                this.loadApiInfoList();
            },
            loadApiInfoList() {
                if (this.apiQueryEntity.entity && this.apiQueryEntity.entity.groupId === '0') {
                    delete this.apiQueryEntity.entity.groupId;
                }
                this.loading = true;
                this.$server.api.getApiPage(this.apiQueryEntity).then((res) => {
                    var {data} = res;
                    this.loading = false;

                    if (data.code == 200) {
                        this.columnDatas = data.data.records;
                        this.apiQueryEntity.total = data.data.total;
                    } else {
                        this.$Message.warning(data.msg);
                    }
                });
            },
            loadApiGroups() {
                this.loading = true;
                this.$server.api
                    .getGroupListByVersionId(this.versionActiveValue)
                    .then((res) => {
                        var {data} = res;
                        this.loading = false;

                        if (data.code == 200) {
                            this.groupItemDatas = this.formatGroupItemDatas(data.data);
                            this.storeGroup();
                            this.groupActiveValue = this.groupItemDatas[0].value;
                        } else {
                            this.$Message.warning(data.msg);
                        }
                    });
            },
            initProjectAndVersions() {
                this.loading = true;
                this.$server
                    .multi(
                        this.$server.project.getProjectById(this.projectId),
                        this.$server.project.proVersionList(this.projectId)
                    )
                    .then((results) => {
                        var data1 = results[0].data;
                        var data2 = results[1].data;
                        this.loading = false;
                        if (data1.code == 200 && data2.code == 200) {
                            this.projectInfo = data1.data;
                            this.versionItemDatas = this.formatVersionData(data2.data);
                            this.versionActiveValue = this.versionItemDatas[0].value;
                        } else {
                            this.$Message.warning(data1.msg | data2.msg);
                        }
                    });
            },
            formatGroupItemDatas(datas) {
                const result = [
                    {
                        icon: "md-git-branch",
                        label: "全部",
                        value: "0",
                        defActive: true,
                    },
                ];

                if (!datas || datas.length == 0) {
                    return result;
                }

                for (var item of datas) {
                    result.push({
                        icon: "md-git-branch",
                        label: item.groupName,
                        value: item.id,
                    });
                }
                return result;
            },
            formatVersionData(datas) {
                if (!datas || datas.length == 0) {
                    return [];
                }
                const result = [];
                for (var item of datas) {
                    result.push({
                        icon: "md-git-branch",
                        label: item.versionInfo,
                        value: item.id,
                    });
                }

                if (result.length > 0) {
                    result[0].defActive = true;
                }

                return result;
            },
            deleteVersion(row) {
                this.$Modal.confirm({
                    title: "删除",
                    content: `<p>是否删除 '${row.label}' 这个项目版本?</p>`,
                    loading: true,
                    onOk: () => {
                        this.$server.project.deleteProjectVersion(row.value).then((res) => {
                            const {data} = res;
                            if (data.code == 200) {
                                this.$Modal.remove();
                                this.$Message.success("删除成功");
                                this.initProjectAndVersions();
                            } else {
                                this.$Message.warning(data.msg);
                            }
                        });
                    },
                });
            },
            deleteGroup(row) {
                this.$Modal.confirm({
                    title: "删除",
                    content: `<p>是否删除 '${row.label}' 这个分组?</p>`,
                    loading: true,
                    onOk: () => {
                        this.$server.project.deleteProjectGroup(row.value).then((res) => {
                            const {data} = res;
                            if (data.code == 200) {
                                this.$Modal.remove();
                                this.$Message.success("删除成功");
                                this.loadApiGroups();
                            } else {
                                this.$Message.warning(data.msg);
                            }
                        });
                    },
                });
            },
            versionItemToolsRender(h, {row, index, count}) {
                const btns = [];

                if (count > 1) {
                    btns.push(
                        h("Icon", {
                            props: {
                                type: "md-trash",
                                size: "18",
                            },
                            on: {
                                click: () => {
                                    this.deleteVersion(row);
                                },
                            },
                        })
                    );
                }

                return h(
                    "span",
                    {
                        class: {
                            "list-hover": true,
                        },
                    },
                    btns
                );
            },
            groupItemToolsRender(h, {row, index}) {
                const btns = [];

                if (row.label !== "全部") {
                    btns.push(
                        h("Icon", {
                            props: {
                                type: "md-trash",
                                size: "18",
                            },
                            on: {
                                click: () => {
                                    this.deleteGroup(row);
                                },
                            },
                        })
                    );
                }

                return h(
                    "span",
                    {
                        class: {
                            "list-hover": true,
                        },
                    },
                    btns
                );
            },
        },
        created() {
            this.$bus.$on("api-list::initDatas", () => {
                this.projectId = this.$route.query.projectId;
                this.initProjectAndVersions();
            });

            this.$bus.$on("api-list::initApiList", () => {
                this.apiQueryEntity.pageNum = 1;
                this.loadApiInfoList();
            });

            this.$bus.$emit("api-list::initDatas");
        },
    };
</script>

<style lang="scss" scoped>
    .api-list {
        height: calc(100vh - 50px);
        overflow-y: auto;
        background-color: #ecf0f5;

        .content-main {
            padding: 15px;
        }

        .projectBlockBtn {
            outline: none;
            display: block;
            background-color: #3c8dbc;
            padding: 6px;
            width: 100%;
            color: #fff;
            text-align: center;
            border: 1px solid transparent;
            border-radius: 3px;
            transition: color 0.2s linear, background-color 0.2s linear;

            .version-box {
                font-size: 12px;
                color: coral;
            }

            &:hover {
                background-color: #367da7;
            }

            &:active {
                background-color: #2f6c8f;
            }
        }
    }

    .add-btn {
        background-color: #449fd4;
        border-color: #449fd4;

        &:hover {
            background-color: #367da7;
            border-color: #367da7;
        }

        &:active {
            background-color: #2f6c8f;
            border-color: #2f6c8f;
        }
    }

    .list-hover {
        &:hover {
            transform: scale(1.1);
            color: rgb(110, 107, 107);
        }
    }
</style>