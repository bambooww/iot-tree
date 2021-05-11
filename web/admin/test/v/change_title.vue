<template>
  <div>
    <em>Change the title here:</em>
    <input type="text" :value="title" @input="onInput">
  </div>
</template>
 
<script>
export default {
  props: ['title'],
  methods: {
    onInput(event) {
      this.$emit('input', event.target.value);
    }
  }
}
</script>