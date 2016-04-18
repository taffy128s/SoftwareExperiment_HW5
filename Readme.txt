這次作業就單純照著spec做，利用上次寫好的東西，再加上新學的socket讓它們互聯
基本上就是server等雙方來連線，連完後server會給予檔案名稱供雙方開啟
雙方會各自輸入input，再個別傳至server，由server判定兩者是否相同
如果相同則前進並換下個隨機字，如果不相同就再輸入一次，直至破關為止

遇到的問題:
1. """每次write記得都要在結尾加換行"""
	這很重要，如果沒有的話，read方在readline時會讀不到
	這個搞了我很久
2. """不能在keylistener那裏read"""
	這也很重要，如果在keylistener作read的話，會讓整個program停頓
	我記得好像是因為java有一個thread在處理這類東西，如果在這個裡call read
	會讓所有東西都被block住
以上