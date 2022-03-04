export default {
    name: "auto-complete-wrap",
    data() {
        return {
            matcheDatas: this.datas
        }
    },
    props: {
        handleSearchFn: {
            required: false,
            type: Function,
            default: (val, datas) => {
                var newArray = [];
                for (var item of datas) {
                    if (item.toUpperCase().indexOf(val.toUpperCase()) !== -1) {
                        newArray.push(item);
                    }
                }
                return newArray;
            }
        },
        datas: {
            type: Array,
            required: true
        },
        value:{
            type:String,
            required:false,
            default:()=>""
        },
        placeholder:String
    },
    methods: {
        handleSearch(val) {
            this.matcheDatas = !val ? this.datas : this.handleSearchFn(val, this.datas);
        }
    },
    render(h) {
        return h('AutoComplete', {
            on:{
                input:(val)=>{
                    this.$emit('input',val);
                },
                'on-search':(val)=>{
                    this.handleSearch(val);
                }
            },
            props:{
                value:this.value,
                data:this.matcheDatas,
                placeholder:this.placeholder
            }
        });
    },
}