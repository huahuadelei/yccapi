<template>
    <div class="my-card" :class="{'top-color':topColor}">
        <div class="header" :style="{'min-height': headerHeight+'px'}" v-if="title||$slots.title||$slots.extra">
            <div class="title">
                <template v-if="title&&!$slots.title">{{title}}</template>
                <slot v-else name="title"></slot>
            </div>
            <div class="extra">
                <slot name="extra"></slot>
            </div>
        </div>
        <div class="content">
            <slot></slot>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'my-card',
        data() {
            return {
                
            }
        },
        props:{
            title:{
                type:String,
                required:false,
                default:()=>null
            },
            'top-color':{
                type:Boolean,
                required:false,
                default:()=>true
            },
            'header-height':{
                type:Number,
                required:false,
                default:()=>40
            }
        },
        methods: {
            
        },
    }
</script>

<style lang="scss" scoped>
    .my-card{
        width: 100%;
        min-height: 60px;
        background-color: #ffffff;
        border-radius: 3px;
        box-shadow: 0 0 2px rgba($color: #000000, $alpha: .1);
        font-family: "Arial","Microsoft YaHei","黑体","宋体",sans-serif;

        &.top-color{
            border-top: 3px solid #367FA9;
        }
        .header{
            border-bottom: 1px solid #ecebeb;
            .title{
                float:left;
                height: inherit;
                padding: 7px;
                font-size: 18px;
            }
            .extra{
                float:right;
                height:40px;
                padding-right: 2px;

            }
        }
        .content{
            padding: 15px;
        }
    }
</style>