# filemanager
![filemanager](https://github.com/ramilxyz/filemanager/blob/master/%D0%A1%D0%BD%D0%B8%D0%BC%D0%BE%D0%BA%20%D1%8D%D0%BA%D1%80%D0%B0%D0%BD%D0%B0_2021-02-26_22-31-49.png?raw=true)

менеджер файлов для openssl.org. (скачивание, просмотр MD5)

стек: kotlin, coroutine, livedata, room, retrofit2, mvvm

особенности: архитектура Model-View-ViewModel. 
файлы сохраняются локально но в приложение все равно добавил бд room чтобы сохранять состояние файлов,
по этому может работать и оффлайн, но при оффлайн только просмотр скаченных файлов, и просмотр списка 
существующих файлов на сервере. локализация на английском-русском.
добавил кастомные вью
