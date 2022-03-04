<template>
  <div class="my-list">
    <div class="header">
      <div class="title">
        <template v-if="title">{{ title }}</template>
        <slot name="title" v-else></slot>
      </div>

      <div class="extra">
        <slot name="extra"></slot>
      </div>
    </div>

    <div class="content">
      <ul>
        <template v-for="(item, index) in itemDataLists">
          <li
            :class="{ active: item.value == value }"
            :key="index"
          >
            <div class="content"  @click="()=>{$emit('input',item.value);emitItemClick(item, index)}">
                  <Icon
                  v-if="item.icon"
                    :type="item.icon"
                    style="font-weight: 700; margin-right: 5px"
                  />{{ item.label }}
            </div>
            <div class="tools" v-if="itemToolsRender">
                 <MyRender
                  :scoped="{row:item,index:index,count:itemDataLists.length}"
                  :renderFn="itemToolsRender"
                />
            </div>
          </li>
        </template>
      </ul>
    </div>

  </div>
</template>

<script>
import MyRender from '../MyRender'
export default {
  name: "my-list",
  components:{
    MyRender
  },
  data() {
    return {
      slotItemDatas:[],
    };
  },
  updated(){
    if(this.activeItem){
      this.emitItemClick(this.activeItem);
    }
  },
  computed:{
    itemDataLists(){
        if(!this.itemDatas){
           return this.slotItemDatas;
        }else{
            return this.itemDatas;
          }
    },
    activeItem(){
      return this.getItem(this.value);
    }

  },
  created() {
    this.initSlotItemDatas();
  }
  ,
  props: {
    title: {
      type: String,
      required: false,
      default: () => null,
    },
    itemDatas:{
      type:Array,
      required:false,
      default:()=>null
    },
    value:{
      type:String,
      required:false,
      default:()=>null
    },
    itemToolsRender:{
      type:Function,
      required:false,
      default: undefined
    }
  },
  methods: {
    getItem(value){
      for(var item of this.itemDataLists){

        if(item.value == value){

          return item;
        }
      }
      return null;
    },
    initSlotItemDatas(){
          var defSlots = this.$slots.default;
            if (!defSlots) {
              return;
            }
            const newArray = [];

            for (var item of defSlots) {
              if (
                item.data &&
                item.componentOptions.Ctor.extendOptions.name === "my-list-item"
              ) {
                var data = { ...item.componentOptions.propsData };
                newArray.push(data);
   
              }
            }
            this.slotItemDatas =  newArray;
    },
    emitItemClick(item,index) {
      if (this.active === item) {
        return;
      }
      this.active = item;
      this.$emit("on-item-click", item);
    },
  },
};
</script>

<style lang="scss" scoped>
.my-list {
  background-color: #fff;
  min-height: 60px;
  width: 100%;
  display: inline-block;
  user-select: none;
  border-radius: 3px;
  border: none;
  .header {
    position: relative;
    width: inherit;
    height: 40px;
    border-bottom: 1px solid #f4f4f4;
    .title {
      padding: 5px;
      float: left;
      font-size: 18px;
      height: inherit;
    }
    .extra {
      float: right;
      height: inherit;
      padding: 5px;
    }
  }

  .content {
    ul {
      margin: 0;
      padding: 0;

      & > li {
        // cursor: pointer;
        list-style-type: none;
        border-left: 3px solid transparent;
        display: flex;
        flex-direction: row;

        .content{
          flex: 1;
          padding: 8px 15px;
          display: inline-block;
        }
        .tools{
          flex: .1;
          padding: 8px 15px;
          display: inline-block;
        }
        & + li {
          border-top: 0.1px solid #f4f4f4;
        }
        &:hover {
          background-color: rgb(248, 247, 247);
        }
        &.active {
          background-color: rgb(248, 247, 247);
          border-left-color: #367fa9;
          font-weight: bold;
        }
      }
    }
  }
}
</style>