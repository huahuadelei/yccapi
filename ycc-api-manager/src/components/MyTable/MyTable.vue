<template>
    <div class="my-table">

        <div class="header">
            <div class="full-left">
                 <slot name="header-left"></slot>
            </div>
            <div class="full-right">
                <slot name="header-right"></slot>
            </div>
        </div>

        <template v-if="iviewTable">
            <Table :columns="columns" :data="columnDatas"></Table>
        </template>
        <template v-else>
                <table class="table">
                <thead>
                    <tr class="text-full-left">
                        <th  v-for="(item,index) in columns" :key="index">{{item.title}}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr class="text-full-left" v-for="(row,rowIndex) in columnDatas" :key="rowIndex"> 
                        <td  v-for="(item,index) in columns" :key="index">
                            {{row[item.key]}}

                        </td>
                    </tr>
                </tbody>
            </table>
        </template>
       
       
        <div class="floor">
           <slot name="floor"></slot>
        </div>


    </div>
</template>

<script>
    export default {
        name: 'my-table',
        data() {
            return {
                
            }
        },
        props:{
            columns:{
                type:Array,
                required:true
            },
            columnDatas:{
                type:Array,
                required:true
            }
        },
        computed:{
            iviewTable(){
                var attrs = this.$attrs;
                if(attrs&&attrs.hasOwnProperty('iviewTable')){
                    return true;
                }
                return false;

            }
        }
    }
</script>

<style lang="scss" scoped>
    .my-table{
        width: 100%;
        min-height: 40px ;
        background-color: #fff;
        border: none;
        border-top: 3px solid #367fa9;
        border-radius: 3px;
        border-collapse:collapse;

        .header{
            min-height: 40px;
            line-height: 34px;
            border-bottom: 1px solid #ebe9e9;
            padding: 2px 10px;
        }
        .floor{
             min-height: 40px;
            line-height: 34px;
            border-bottom: 1px solid #ebe9e9;
            padding: 2px 10px;
        }

        .table{
            width:100%;
           & tr{
                height: 30px;
                &:hover{
                    background-color: fuchsia;
                }
            }

            thead,tbody,tr{
                border: 1px solid #ca3737;

            };
            th,td{
                padding-left: 5px;

                padding-right: 5px;
            }
        }
    }
</style>
