<script>
    export default {
        name: 'my-list-item',
        props:{
            icon:{
                type:String,
                required:false,
                default:()=>null
            },
            label:String,
            value:Object
        }
    }
</script>
