<template>
    <div class="view-counter">
          <MyCard>
        <div class="counter-box">
            <div>
                <p><span class="item-title">视图总数</span></p>
                <p >
                    <span class="item-value" v-if="counter.total!=null">{{counter.total}}</span>
                    <span v-else style="display:inline-block;margin-top:8px;font-weight:700">加载中...</span>
                </p>
            </div>
            <div>
                <p><span class="item-title">已发布的视图</span></p>
                <p >
                     <span class="item-value" v-if="counter.published!=null">{{counter.published}}</span>
                    <span v-else style="display:inline-block;margin-top:8px;font-weight:700">加载中...</span>
                </p>
            </div>
            <div>
                <p><span class="item-title">已作废的视图</span></p>
                <p >
                      <span class="item-value" v-if="counter.obsolete!=null">{{counter.obsolete}}</span>
                    <span v-else style="display:inline-block;margin-top:8px;font-weight:700">加载中...</span>
                </p>
            </div>
        </div>
    </MyCard>
    </div>
</template>

<script>
import MyCard from '@/components/MyCard'
    export default {
        name: 'view-counter',
        components:{
            MyCard
        },
        props:{
            counter:{
                type:Object,
                required:true
            }
        },
        data() {
            return {
                
            }
        }
    }
</script>

<style lang="scss" scoped>
       .counter-box{
        height: 60px;
        width: 100%;
        display: flex;
        flex-direction: row;
        justify-content: center;
        position: relative;
        & > div{
            color:rgb(99, 98, 98);
            padding: 2px;
            text-align: center;
            height: inherit;
            width: 100%;

            &  .item-title{
                font-size:17px;
                font-family: 'Trebuchet MS', 'Lucida Sans Unicode', 'Lucida Grande', 'Lucida Sans', Arial, sans-serif;
                font-weight: bold;
                text-shadow: 0 0 3px rgba($color: #000000, $alpha: .2);
            }
            & .item-value{
                margin-top: 15px;
                font-size: 23px;
                font-weight: bold;
                text-shadow: 0 0 3px rgba($color: #000000, $alpha: .2);

            }
        }

    }
</style>