適用内容
- 剣: iron_sword, golden_sword を追加
- 道具: diamond/iron/golden の pickaxe, shovel を追加
- 左バッジ: swords=Sharpness(1-5), tools=Efficiency(1-5)
- 右バッジ: swords=Fire Aspect(1-2), tools=Fortune(1-3)
- 左上バッジ: swords=Knockback(1-2), toolsは未使用(常に0)

適用手順
1. この zip の src フォルダを既存プロジェクトに上書きコピーしてください。
2. もし src/main/java/org/example2/onegai/client/OnegaiClient.java が残っていたら削除してください。
3. 一度 build フォルダを削除し、gradlew clean のあと runClient してください。

補足
- diamond_sword 用の既存ファイルはそのまま残しています。
- *_selector.json は今回の追加には使っていません。
- bow / enchanted_book / armor overlay はまだ未対応です。
