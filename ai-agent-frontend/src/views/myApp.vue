<template>
  <div class="chat-room">
    <h2>AI 旋转机械故障诊断大师</h2>
    <div class="chat-history" ref="chatHistory">
      <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
        <div class="content">{{ msg.content }}</div>
      </div>
    </div>
    <div class="input-area">
      <input v-model="inputMessage" @keyup.enter="sendMessage" placeholder="输入您的消息..." />
      <button @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoveApp',
  data() {
    return {
      messages: [],
      inputMessage: '',
      chatId: '',
      eventSource: null
    };
  },
  mounted() {
    this.chatId = 'chat_' + Date.now(); // 自动生成chatId
    this.scrollToBottom();
  },
  methods: {
    sendMessage() {
      if (!this.inputMessage.trim()) return;
      this.messages.push({ role: 'user', content: this.inputMessage });
      this.connectSSE(this.inputMessage);
      this.inputMessage = '';
      this.scrollToBottom();
    },
    connectSSE(message) {
      const url = `http://localhost:8123/api/ai/love_app/chat/sse?message=${encodeURIComponent(message)}&chatId=${this.chatId}`;
      this.eventSource = new EventSource(url);

      let aiMessageIndex = this.messages.push({ role: 'ai', content: '' }) - 1;

      this.eventSource.onmessage = (event) => {
        const data = event.data;
        if (data === '[DONE]') {
          this.eventSource.close();
        } else {
          this.messages[aiMessageIndex].content += data;
          this.scrollToBottom();
        }
      };

      this.eventSource.onerror = (error) => {
        console.error('SSE error:', error);
        this.eventSource.close();
      };
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const chatHistory = this.$refs.chatHistory;
        chatHistory.scrollTop = chatHistory.scrollHeight;
      });
    }
  },
  beforeUnmount() {
    if (this.eventSource) {
      this.eventSource.close();
    }
  }
};
</script>

<style scoped>
.chat-room {
  max-width: 600px;
  margin: 0 auto;
  border: 1px solid #ccc;
  padding: 20px;
  height: 80vh;
  display: flex;
  flex-direction: column;
}
.chat-history {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 10px;
}
.message {
  margin: 10px 0;
  padding: 10px;
  border-radius: 10px;
  max-width: 80%;
}
.user {
  background-color: #dcf8c6;
  align-self: flex-end;
  margin-left: auto;
}
.ai {
  background-color: #f1f0f0;
  align-self: flex-start;
  margin-right: auto;
}
.content {
  word-break: break-word;
}
.input-area {
  display: flex;
}
input {
  flex: 1;
  padding: 10px;
}
button {
  padding: 10px 20px;
  cursor: pointer;
}
</style>