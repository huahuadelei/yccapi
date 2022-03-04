<template>
    <div class="error_404 text-not-select">
        <img src="@/assets/img/timg.jpg" style="width:600px;height:auto" alt="">
        <div>
            <p><h1>页面可能是丢失了&nbsp;&nbsp;您可以选择返回首页</h1></p>
            <a href="javaScript:" class="back-btn" @click="$router.push({name:'home'})">返回首页</a>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'error_404',
        data() {
            return {
                
            }
            
        },methods: {
            back(){
                alert(1)
            }
        },
    }
</script>

<style lang="scss" scoped>
    .error_404{
        height: inherit;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: flex-start;
        padding: 35px;

        .back-btn{
            display: inline-block;
            background-color: black;
            padding: 0px 60px;
            border-radius: 40px;
            height: 40px;
            line-height: 40px;
            transform: translate(150px,50px);
            color: #fff;
            outline: none;
            transition: background-color .6s;
            &:hover{
                background-color: rgb(133, 133, 133);
                transition: background-color .6s;
            }
        }
    }
</style>