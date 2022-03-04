<template>
    <div class="view-list-items" v-if="itemDatas&&itemDatas.length>0">
        <div class="list-item" v-for="(item,index) in itemDatas" :key="index">
            <div class="content">
                <p>
                    <label class="title">{{item.viewName}}</label> 
                    <span class="status status-success" v-if="item.surplusDays && item.surplusDays >=0 ">使用中</span>
                    <span class="status status-error" v-if="!item.surplusDays || item.surplusDays < 0 ">已作废</span>
                </p>

                <p>
                    <div class="ele-box">
                        <div class="ele-item">
                          <label> 项目名称：</label>{{item.projectName}}
                        </div>
                        <div class="ele-item">
                          <label> 剩余时间：</label>
                          <template v-if="item.isExp==1"> 
                                 <template v-if="item.surplusDays>=0">
                                    {{item.surplusDays}}天
                                </template>
                                <template v-else>0天</template>
                          </template>
                          <template v-else>永久</template>
                        </div>
                        <div class="ele-item">
                          <label> 访问密码：</label>
                          <template v-if="item.hasPass == 1">
                              <Tag>{{item.showPass}}</Tag>
                          </template>
                          <template v-else>
                              <Tag>无</Tag>
                          </template>
                         
                        </div>
                        <Tag color="volcano" style="margin-left:15px" v-if="item.isTestable===1">TEST</Tag>
                    </div>
                </p>

                <p>
                  <div style="margin-top:6px">
                      <label>
                          版本分支：
                      </label>
                      <ul class="versions-box">
                          <li v-for="(v,index) in item.versionList" :key="index">
                              <span class="v-item">
                                {{v.versionName}}&nbsp;({{v.versionApiCounter}})
                             
                             </span>
                          </li>
                      </ul>
                  </div>
                   
                </p>
                
            </div>
            <div class="tools" v-if="item.surplusDays && item.surplusDays >=0 && item.versionList&& item.versionList.length>0">
               <span class="show-btn" @click="showBtnClick({row:item,index:index})">查看</span>
               <span class="show-btn" @click="exportBtnClick({row:item,index:index})">导出</span>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'view-list-items',
        props:{
            itemDatas:{
                type:Array,
                required:true
            }
        },
        data() {
            return {
                
            }
        },
        methods: {
            showBtnClick(scope){
                this.$emit('show-btn-click',scope);
            },
            exportBtnClick(scope){

                this.$emit('export-btn-click',scope);
            }
        },
    }
</script>

<style lang="scss" scoped>
    .view-list-items{
        padding: 4px;
        width: 100%;
        position: relative;
        .list-item{
            display: flex;
            flex-direction: row;
            justify-content: center;
            position: relative;
            border-top: 1px solid #EAEAEA;
            padding: 8px;
           
           
            .content{
                flex:8;
                font-family:  PingFang SC,Tahoma,Arial;
                .title{
                   font-size: 16px;
                   min-width: 80px;
                   display: inline-block;
                   color: #6ebca2;
                }
                .status{
                    display: inline-block;
                    padding: 2px 10px;
                    border-radius: 20px;
                    font-size: 12px;
                    margin-left: 10px;
                    height: 22px;
                    line-height: 16px;
                    box-sizing: border-box;

                }
                .status-success{
                    background: #ecf6ff;
                    color: #6ebca2;
                    border: 1px solid #6ebca2;
                }
                .status-error{
                    background: #ffecec;
                    color: #ee5e5e;
                    border: 1px solid #fd7070;
                }
                .ele-box{
                    display: flex;
                    flex-direction: row;
                    justify-content: flex-start;
                    flex-wrap: wrap;
                    align-items: center;
                    margin-top: 10px;
                    .ele-item{
                        font-size: 15px;
                        width: 200px;
                    }
                }
               
            }
            .tools{
                width: 200px;
                position: relative;
                text-align: center;
                padding-top: 35px;
              
                .show-btn{
                    display: inline-block;
                    position: relative;
                    padding: 2px 8px;
                    font-size: 15px;
                    color: #6ebca2;
                    cursor: pointer;
                    transform: translate(-15px,-15px);
                     transform: scale(1.1);
                     border-radius: 15px;
                     border:1px solid #6ebca2;
                     &:hover{
                        border-color: #fd7070;
                        color: #fd7070;
                     }

                    &+.show-btn{
                        margin-left: 15px;
                    }
                }
            }
        }
        .list-item:nth-last-child(1){
            border-bottom: 1px solid #EAEAEA;
        }

        .versions-box{
            display: inline-block;
            &>li{
                list-style-type: none;
                display: inline-block;

                &+li{
                    margin-left:15px ;
                }
                .v-item{
                    font-size: 13px;
                    position: relative;
                    display: inline-block;
                    min-width: 70px;
                    padding: 1px 10px;
                    border-radius:20px;
                    border: 1px solid rgb(100, 102, 102);
                    background-color: rgb(228, 225, 225);
                    color: rgb(128, 129, 129);
                    box-shadow: 0 0 4px rgba($color: #000000, $alpha: .3);
                    
                }
            }
        }
    }
</style>