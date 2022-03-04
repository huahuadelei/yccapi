<template>
    <div class="my-loading loading-zhezhao" v-if="show">
          <vue-loading :isLoading='show' :type="'loading'+type" :text="text" scale='.8' ></vue-loading>
    </div>
</template>

<script>
    export default {
        name: 'my-loading',
        data() {
            return {
                
            }
        },
        props:{
            show:{
                type:Boolean,
                required:true
            },
            text:{
                type:String,
                required:false,
                default:()=>'加载中..'
            },
            type:{
                type:String,
                required:false,
                default:()=>'1'
            }
        }
    }
</script>

<style lang="scss" scoped>

    .loading-zhezhao{
        background-color: rgba($color: #fff, $alpha: .9);
        position: absolute;
        left: 0;
        right: 0;
        bottom: 0;
        top: 0;
        z-index: 999;
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
        
    }
</style>