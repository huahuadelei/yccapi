<template>
    <div class="my-search" @click="focus($event)">
        <input type="text" ref="text" :placeholder="placeholder" :value="value" @input="onInput($event)"  v-bind="$attrs" @keydown.enter="onSearch">

        <span class="search-button" @click="onSearch">
            <Icon type="md-search" size="33"/>
        </span>
    </div>
</template>

<script>
    export default {
        name: 'my-search',
        data() {
            return {
            }
        },
        props:{
            value:String,
            placeholder:String
        },
        methods: {
            focus(e){
                this.$refs['text'].focus();
            },
            onInput(e){
                this.$emit('input',e.target.value);
            },
            onSearch(){
                this.$emit('on-search');
            }
        },
    }
</script>

<style lang="scss" scoped>
    .my-search{
        display: flex;
        flex-direction: row;
        position: relative;
        padding: 5px 25px;
        padding-right: 50px;
        border-radius: 50px;
        height: 50px;
        width: 480px;
        border: 2px solid #000;
        cursor: text;

        
        input{
            border: none;
            outline: none;
            font-size: 20px;
            flex: 1;
            font-family: 'Trebuchet MS', 'Lucida Sans Unicode', 'Lucida Grande', 'Lucida Sans', Arial, sans-serif;
        }
        .search-button{
            box-sizing: border-box;
            display: inline-block;
            position: absolute;
            width: 50px;
            height: 50px;
            right: -2px;
            top: -2px;
            border-top-right-radius: 50px;
            border-bottom-right-radius: 50px;
            border: 2px solid #000;
            border-left:none;
            padding: 6px 5px;
            cursor: pointer;
            &:hover{
                color: #63D5BB;
            }
        }
    }
</style>