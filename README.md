# 文字コードをShift-JISに変換するツール
ドラッグアンドドロップされたテキストファイル (Shift-JIS or UTF-8) を
問答無用で Shift-JIS のテキストファイルに変換するだけの簡単なプログラムです。

UTF-8に存在するがShift-JISには存在しない文字を置換することもできます。

- Windows用: [text-encoder-win.zip](https://raw.github.com/hogelog/text-encoder/master/dist/text-encoder-win.zip)
- Mac用: [text-encoder-mac.zip](https://raw.github.com/hogelog/text-encoder/master/dist/text-encoder-mac.zip)

## Windowsの場合
ZIPを展開したフォルダを適当な場所に保存。
text-encoder.exeへのショートカットを「送る」メニューに
登録しておくと便利かもしれません。

### 送るメニューへのショートカットの登録の仕方
- Windowsキー+Rキーで「ファイル名を指定して実行」ダイアログを開く
- 「shell:sendto」と入力して開く。
- 開かれたフォルダにtext-encoder.exeファイルのショートカットを
「テキストをSJISに変換」などという名前で置く。
- 変換したいテキストファイルを右クリックして「送る」メニュー内に
「テキストをSJISに変換」という項目が増えていることを確認する。

## Macの場合
Automatorとかでゴニョゴニョ
