<template>
    <div class="my-body-params">
        <Row v-for="(item,index) in datas" :key="index" style="padding: 10px;border: 3px dashed transparent;" :style="{'border-color':(item.type=='Object'?'#dfe1e1':'transparent')}">
        <Col span="2" class="text-full-right" >
            <label class="my-label">参数</label>
        </Col>

        <Col span="4">
        <Input v-model="item.name" placeholder="请输入参数名"/>

        </Col>

        <Col span="2"  class="text-full-right" >
            <label class="my-label">说明</label>
        </Col>

        <Col span="4">
            <Input v-model="item.desc" placeholder="请输入描述信息"/>
        </Col>

        <Col span="2"  class="text-full-right" >
            <label class="my-label">类型</label>
        </Col>

        <Col span="4">
            <Select v-model="item.type" filterable>
                <Option value="Object"  >Object</Option>
                <Option v-for="item in dataTypes" :value="item.typeName" :key="item.id" >{{item.typeName}}</Option>
            </Select>
        </Col>

        <Col span="3" offset="1">
            <Select v-model="item.required" v-if="item.type!=='Object'">
                <Option :value="1" >必填</Option>
                <Option :value="0" >非必填</Option>
            </Select>
            <template v-else>
            <Button long @click="addProperty(item)">添加属性</Button>
            </template>
        </Col>

        <Col span="1">
            <span class="del-btn" @click="datas.splice(index,1)">
                <Icon type="md-close" size="18"/>
            </span>
        </Col>
        <Col span="24" v-if="item.type=='Object'&&item.propertys&&item.propertys.length>0" :style="{'margin-top':(item.type=='Object'?'15px':'0')}">
            <my-body-params :datas="item.propertys" :dataTypes="dataTypes"/>
        </Col>
    </Row>
    </div>
</template>

<script>
    export default {
        name: 'my-body-params',
        data() {
            return {
                
            }
        },
        props:{
            datas:{
               type:Array,
               required:true 
            },
            dataTypes:{
                 type:Array,
               required:true 
            }
        },
        methods: {
            addProperty(item){
               item.propertys.push({name:'',desc:'',type:'String',required:1,propertys:[]})
            }
        },
    }
</script>

<style lang="scss" scoped>
    .my-body-params{
        &+.my-body-params{
            border-top-color: transparent;
        }
    }


    .my-label {
    position: relative;
    top: 5px;
    right: 15px;
    font-family: "Source Sans Pro", "Helvetica Neue", Helvetica, Arial,
      sans-serif;
    font-weight: 700;
  }

  .del-btn {
    display: inline-block;
    padding: 2px;
    border-radius: 50%;
    width: 22px;
    height: 22px;
    cursor: pointer;
    position: relative;
    left: 20px;
    top: 5px;
    &:hover {
      background-color: rgb(73, 114, 160);
      color: #fff;
    }
  }
</style>