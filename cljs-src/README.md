# [Gooreplacer](https://github.com/jiacai2050/gooreplacer/)

> Redirect, block Requests; Modify, cancel request/response Headers.


## Note for Mozilla addon's reviewer

This addon is written in ClojureScript, so [lein](https://leiningen.org/) is required.

```sh
wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
chmod +x lein
sudo mv lein /usr/local/bin
```
After install, you can run `lein -v` to check everything is OK
```sh
$ lein -v
Leiningen 2.9.10 on Java 17.0.4 OpenJDK 64-Bit Server VM
```

In order to get the final zip file, run `./package.sh`, it will output the zip file in your home dir, something like `firefox_gooreplacer_${date +%s}.zip`
