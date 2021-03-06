





IOT-Tree Serverとは
==







IOT-Tree Serverは、モノのインターネット接続、データの整備、ヒューマンマシンのインタラクティブな展示のサービスソフトウェアシステムです。

通信アクセス、チャネル、デバイスドライバ、データラベルなどの複数の階層を含む明確なアーキテクチャを使用して構築されています。ユビキタスネットワークシステムを様々な面で統一的に木状に管理し、組織する。

このツリー管理の基礎の上で、IOT-Tree ServerはWeb方式に基づくオンラインコンポーネントインタフェース(ヒューマンマシンインタフェース)の設計、配置とオンライン応用を提供した。

IOT-Tree Serverは、Javaを使用して開発されたオープンソースソフトウェアです。その管理インタフェースは完全にWeb方式に基づいている。







[クイックスタート][quick_start]

[クイックスタート][quick_link]

[ケーススタディ][demo_link]

<a href="https://github.com/bambooww/iot-tree/releases" target="_blank">ダウンロード</a>





# 1 IOT-Tree Serverは何ができるのか







## 1.1 工業現場のコンポーネントソフトウェアとして

工業現場のPC側コンポーネントソフトウェアとしてIOT-Tree Serverを使用できます。

フィールド通信状況により適切なアクセス方式を選択し、適切な駆動および関連装置を選択する。現場プロジェクトを迅速にコンピュータ管理側で秩序正しく明確にすることができます。次に、実行状況に基づいて、ヒューマン・マシン・インタラクティブ・コンポーネント・インタフェースを迅速に構成できます。

デバイスまたはサブステーションが複雑な場合は、サブの制御インタフェースを設計してから、全体の制御インタフェースで参照できます。

デバイスが複雑で、独自に提供できるインタフェース要素が定義されている場合は、このデバイスを導入するだけで、デバイスに関連付けられたインタフェース要素はプロジェクトで参照される内容になります。



<img src="./doc/img/prj3.png">






### 参考事例

<div style="display:none">[ライトコントロール][lamp_demo]</div>

<div style="display:none">[ポンプ制御][pump_demo]</div>

[工業自動化プレゼンテーション][case_auto_demo]






## 1.2 システムはデバイスを埋め込んでコントローラになることができます

Java仮想マシンを実行するのに十分なパフォーマンスを持つ組み込み機器があれば、IOT-Tree Serverはデバイスをコントローラにするのに便利です。また、このコントローラはオンライン構成とUIの設計をサポートし、オンラインリアルタイム実行サービスを提供することができる。


[組み込みコントローラ例][embed_ctrl_demo] 





## 1.3 システムはクラウド専用のネットワークサーバとして使用できます

インターネット会社であれば、ユビキタスネットワークプロジェクトに接触したばかりです。プロフェッショナル・デバイスへのアクセス、データ管理の経験の欠如に制限されているか、関連デバイス・アクセス・プロトコルとデータ収集管理の開発を最初から検討しています。このプロセスは、開発リソースと時間を同時に消費します。

IOT-Tree Serverを試してみると、データベースサービスのように、彼女をプロジェクトの後ろでサポートされているサーバにすることができます。

もしあなたのアクセスデバイスが私たちの内部に付属しているドライバによってサポートされている場合、IOT-Tree Serverはあなたに驚喜すると思います。

私たちのオープンソースポリシーに従って、あなたのほとんどのプロジェクトは無料で使用できます。重要なのは、時間が経つにつれて、私たちがサポートする駆動がますます多くなるに違いありません。IOT-Tree Serverが多くのコストを節約し、より多くの収益を得ることができることを望んでいます。





## 1.4 システムは分散アプリケーションのノードになり得る

IOT-Tree Serverは、プロジェクト単位で、複数の通信方式に基づいて共有ノードに配置できます。ノードローカルは、独立して実行できる完全な自動化監視フィールドです。ネットワーク内の他のIOT-Tree Serverは、プロジェクトでこのノードを参照して自身の一部となり、このノードの上位レベルのノードとなります。

この分散機能は、地域間で現場を監視する連動を極めて容易にすることができます。各監視現場にブロードバンドがあれば、クラウドメッセージキューサーバに対して安全な通信を行うだけで、上下関係を容易に形成することができ、トップIOT-Tree Serverノードシステムは通信リンクを監視することができ、この通信状態をシステムの通信異常として多くの関連対応機能として利用することができる。





## その他

詳しく知りたい場合は、関連資料を参照してください。

[クイックスタート][quick_start]

[クイックスタート][quick_link]

[ケーススタディ][demo_link]

システムを使用して問題が発生したり、良いアドバイスがあれば、メールでフィードバックしてください。iottree@hotmail.com


[quick_start]: ./doc/quick_start.md
[quick_link]: ./doc/quick/index.md
[demo_link]: ./doc/case/index.md

[dl_link]: https://github.com/bambooww/iot-tree/releases

[pump_demo]: ./doc/case/example_psd.md
[case_auto_demo]: ./doc/case/case_auto.md
[lamp_demo]: ./doc/case/example_lamp_demo.md

[embed_ctrl_demo]: ./doc/case/example_embed.md
