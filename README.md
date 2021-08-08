# <img src="https://github.com/YujiSoftware/msg2txt/raw/main/mail.png" width="32px"> msg2txt

Extract .msg file (Outlook mail file) to a text file.  
Attachments file can also be expanded.

Compatible with macOS / Linux / Windows.

## Download

* Installer (including Java runtime, it will be installed automatically)
    * For macOS: [msg2txt-1.0.0.dmg](https://github.com/YujiSoftware/msg2txt/releases/download/1.0.0/msg2txt-1.0.0.dmg)
    * For Windows: [msg2txt-1.0.0.exe](https://github.com/YujiSoftware/msg2txt/releases/download/1.0.0/msg2txt-1.0.0.exe)
    * For Linux: [msg2txt_1.0.0-1_amd64.deb](https://github.com/YujiSoftware/msg2txt/releases/download/1.0.0/msg2txt_1.0.0-1_amd64.deb)
* Standalone (**not** including Java runtime)
    * zip: [msg2txt-1.0.0.zip](https://github.com/YujiSoftware/msg2txt/releases/download/1.0.0/msg2txt-1.0.0.zip)
    * tar: [msg2txt-1.0.0.tar](https://github.com/YujiSoftware/msg2txt/releases/download/1.0.0/msg2txt-1.0.0.tar)

## How to use (GUI)

Drag and drop .msg file to <img src="https://github.com/YujiSoftware/msg2txt/raw/main/mail.png" width="16px"> icon.

ℹ️ This icon is on the applications (macOS), or desktop (Windows)

## How to use (CUI)

### macOS

```
/Applications/msg2txt.app/Contents/MacOS/msg2txt <.msg file>
```

### Linux

```
/opt/msg2txt/msg2txt <.msg file>
```

### Windows

```
C:\Program Files\msg2txt\msg2txt.exe <.msg file>
```

### Standalone

```
bin/msg2txt <.msg file>
```

## Icon

https://www.iconpacks.net/free-icon/mail-1015.html

## Library

[Apache POI-HSMF](https://poi.apache.org/components/hsmf/index.html) - Java API To Access Microsoft Outlook MSG Files

## License

[Apache License 2.0](https://github.com/YujiSoftware/msg2txt/blob/main/LICENSE)
