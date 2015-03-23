# siga-transportes
Repositório do módulo de transportes. Este módulo é integrado aos módulos do repositório principal do projeto siga.

## Dependências do Projeto
* Módulos siga-play-module e siga-uteis-play-module instalados no repositório local. 
* [Git](https://windows.github.com/)
* Maven ftp://mirror.reverse.net/pub/apache/maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.zip
* [Java JDK 1.7](http://download.oracle.com/otn-pub/java/jdk/7u67-b01/jdk-7u67-windows-x64.exe)
* [JBoss EAP 6.3](http://www.redhat.com/j/elqNow/elqRedir.htm?ref=https://www.jboss.org/download-manager/content/origin/files/sha256/62/627773f1798623eb599bbf7d39567f60941a706dc971c17f5232ffad028bc6f4/jboss-eap-6.2.0.zip)  
* [Play! Framework 1.3.x]

### Variáveis de ambiente
* **MAVEN_HOME**=C:\Desenvolvimento\apache-maven-3.2.1  
* **JAVA_HOME**=C:\Program Files\Java\jdk1.7  
* **JBOSS_HOME**=C:\Desenvolvimento\jboss-eap-6.2
* O trecho abaixo deverá ser **ACRESCENTADO** no final da variável **PATH**:
* **PATH**=;%JAVA_HOME%\bin;%MAVEN_HOME%\bin;

#### Testando variáveis de ambiente
Abra um ***novo*** prompt de comando do Windows (cmd) e execute:
```
java -version
mvn -version
```

Ambos deverão mostrar a versão dos respectivos programas, caso mostre erro, algum desses passos foi configurado incorretamente.

## Deploy
Para gerar o pacote para deploy do siga-transportes, é necessário entrar no diretório siga-transportes e executa a task package do maven.  
PS: (A primeira vez isso pode demorar bastante).
```
   mvn clean package
```

PS: Será preciso ter o siga-play-module instalado no repositório local. Este módulo faz parte do repositório principal do projeto-siga. 

## Versionamento
Nomenclatura de versionamento:
a.b.c.d

* a = grandes alterações (Mudança do EAP 5 para o 6 por exemplo)
* b = alterações médias (esquema de banco, propriedades obrigatórias, etc.). Versão com essa alteração é incompatível com a anterior do mesmo nível
* c = pequenas alterações ou correções
* d= hotfix

Todos os deploys em produção devem partir do ** branch master ** com sua respectiva tag (Obedecendo a nomenclatura acima).

Qualquer alteração em uma tag gerada previamente é denominada como **hotfix**.

## JBoss
### Configuração (As pastas referenciadas abaixo estão no repositório principal dp projeto-siga)
* Deploy do drive oracle JDBC. Copiar o arquivo ***projeto-siga/configuracao/ojdbc6.jar*** para ***%JBOSS_HOME%/standalone/deployments/***
* Substituir o arquivo ***%JBOSS_HOME%/standalone/configuracao/standalone.xml*** pelo arquivo ***projeto-siga/configuration/standalone.xml***.
* Colocar o arquivo ***projeto-siga/configuracao/siga.properties*** em ***%JBOSS_HOME%/standalone/configuration/***

## Play (As pastas referenciadas abaixo estão no repositório principal dp projeto-siga)
* JBoss -> Criar o módulo do Play! Framework, copiando a pasta ***projeto-siga/configuracao/Play/modulo/sigadoc*** em ***%JBOSS_HOME%/modules/***  
* Maven -> Instalar a biblioteca no repositório local do maven executando o arquivo ***projeto-siga/configuracao/Play/maven/configure.bat***

## Fonte do Play
### Compilação
Na pasta framework:
```
 executar ant package
```

### Administração
[Interface de administração](http://localhost:9990)
usuário: admin
password: admin@123

* Start:
Dentro do diretório ***%JBOSS_HOME%/bin executar:
```
standalone.bat
```

* Stop:
```
Ctrl + C
```

