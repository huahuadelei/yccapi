<template>
    <div class="home">
        
            <div class="person"  @mouseenter="mouseenterHover($event)" @mouseleave="mouseout($event)">
                <transition
                    enter-active-class="animated fadeInDown"
                    leave-active-class="animated fadeOutUp"
                    mode="out-in"
                >
                    <span class="bubble-box"  v-if="hover">
                        欢迎~欢迎~！
                    </span>

                     <span class="bubble-box"  v-else >
                         欢迎你的登录,<br/>
                        {{$store.getters.getLoginUser.user.username}}
                    </span>
                </transition>
                <img src="@/assets/img/home02.gif" />
            </div>


           
    </div>
</template>

<script>
var timer;
    export default {
        name: 'home',
        data() {
            return {
                hover:false,
             
            }
        },
        methods:{
            mouseenterHover(e){
                this.hover=true;
                if(timer){
                    window.clearTimeout(timer);
                }
          
            },
            mouseout(e){
               timer = setTimeout(() => {
                    this.hover=false; 
                }, 1000);
            }
        }
    }
</script>

<style lang="scss" scoped>
    .home{
        height: calc(100vh - 50px);
        overflow-y: auto;
        width: 100%;
        position: relative;
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
        img{
            user-select: none;
        }
    }
    .person{
        position: relative;
        overflow: hidden;
        width: 300px;
        min-height:600px ;
        img{
            position: absolute;
            bottom: 0;
            left: -75%;
        }
    }

    .bubble-box{
        display: inline-block;
        min-width: 160px;
        min-height: 160px;
        height: auto;
        position: absolute;
        top: 0;
        left: 50px;
        background-image: url('../../../assets/img/qipao01.png') ;
        background-repeat: no-repeat;
        background-position: 0;
        background-size: 100% auto;
        padding: 50px 25px;

        font-size: 18px;
    }
</style>