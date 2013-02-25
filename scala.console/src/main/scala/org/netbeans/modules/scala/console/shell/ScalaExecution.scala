/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.scala.console.shell

import java.io.File
import java.io.IOException
import org.netbeans.api.project.ui.OpenProjects
import org.openide.DialogDisplayer
import org.openide.NotifyDescriptor
import org.openide.filesystems.FileObject
import org.openide.filesystems.FileUtil
import org.openide.modules.InstalledFileLocator
import org.openide.modules.Places
import org.openide.util.Exceptions
import org.openide.util.Utilities
import scala.collection.mutable.ArrayBuffer

/**
 *
 * @author Caoyuan Deng
 */
object ScalaExecution {

  val SCALA_MAIN_CLASS = "scala.tools.nsc.MainGenericRunner" // NOI18N <- Change
  val SBT_MAIN_CLASS = "sbt.xMain" // NOI18N <- Change
    
//    private static final String WINDOWS_DRIVE = "(?:\\S{1}:[\\\\/])"; // NOI18N
//    private static final String FILE_CHAR = "[^\\s\\[\\]\\:\\\"]"; // NOI18N
//    private static final String FILE = "((?:" + FILE_CHAR + "*))"; // NOI18N
//    private static final String FILE_WIN = "(" + WINDOWS_DRIVE + "(?:" + FILE_CHAR + ".*))"; // NOI18N
//    private static final String LINE = "([1-9][0-9]*)"; // NOI18N
//    private static final String ROL = ".*\\s?"; // NOI18N
//    private static final String SEP = "\\:"; // NOI18N
//    private static final String STD_SUFFIX = FILE + SEP + LINE + ROL;
    
//    private static List<RegexpOutputRecognizer> stdScalaRecognizers;
//
//    private static final RegexpOutputRecognizer SCALA_COMPILER =
//        new RegexpOutputRecognizer(".*?" + STD_SUFFIX); // NOI18N
//
//    private static final RegexpOutputRecognizer SCALA_COMPILER_WIN_MY =
//        new RegexpOutputRecognizer(".*?" + FILE_WIN + SEP + LINE + ROL); // NOI18N
//
//    /* Keeping old one. Get rid of this with more specific recongizers? */
//    private static final RegexpOutputRecognizer SCALA_COMPILER_WIN =
//        new RegexpOutputRecognizer("^(?:(?:\\[|\\]|\\-|\\:|[0-9]|\\s|\\,)*)(?:\\s*from )?" + FILE_WIN + SEP + LINE + ROL); // NOI18N
//
//    public static final RegexpOutputRecognizer SCALA_TEST_OUTPUT =
//        new RegexpOutputRecognizer("\\s*test.*\\[" + STD_SUFFIX); // NOI18N
    
//    private String charsetName;
//    
//    public ScalaExecution(ExecutionDescriptor descriptor) {
//        super(descriptor);
//
//        assert descriptor ne null;
//        
//        if (descriptor.getCmd() eq null) {
//            descriptor.cmd(getScala());
//        }
//
//        descriptor.addBinPath(true);
//    }

  /** Create a Scala execution service with the given source-encoding charset */
//    public ScalaExecution(ExecutionDescriptor descriptor, String charsetName) {
//        this(descriptor);
//        this.charsetName = charsetName;
//    }
    
//    public synchronized static List<? extends RegexpOutputRecognizer> getStandardScalaRecognizers() {
//        if (stdScalaRecognizers eq null) {
//            stdScalaRecognizers = new LinkedList<RegexpOutputRecognizer>();
//            stdScalaRecognizers.add(SCALA_COMPILER_WIN_MY);
//            stdScalaRecognizers.add(SCALA_COMPILER);
//            stdScalaRecognizers.add(SCALA_COMPILER_WIN);
//        }
//        return stdScalaRecognizers;
//    }

  /**
   * Returns the basic Scala interpreter command and associated flags (not
   * application arguments)
   */
//    public static List<String> getScalaArgs(String scalaHome, String cmdName) {
//        return getScalaArgs(scalaHome, cmdName, null);
//    }

  def getSbtArgs(sbtHome: String): (String, Array[String]) = {
    val args = new ArrayBuffer[String]()

    val executable = getJavaHome + File.separator + "bin" + File.separator + "java" // NOI18N   
    // XXX Do I need java.exe on Windows?

    args += "-Xmx512M"
    args += "-Xss1M"
    args += "-XX:+CMSClassUnloadingEnabled"
    args += "-XX:MaxPermSize=256M"
        
    args += "-Dsbt.log.noformat=true"
    /** 
     * @Note:
     * jline's UnitTerminal will hang in my Mac OS, when call "stty(...)", why? 
     * Also, from Scala-2.7.1, jline is used for scala shell, we should 
     * disable it here by add "-Djline.terminal=jline.UnsupportedTerminal"?
     * And jline may cause terminal unresponsed after netbeans quited.
     */
    //args += "-Djline.terminal=jline.UnsupportedTerminal" 
    args += "-Djline.WindowsTerminal.directConsole=false"
            
    // TODO - turn off verifier?

    // Main class
    args += "-jar"
    args += getSbtLaunchJar(sbtHome).getAbsolutePath // NOI18N

    // Application arguments follow
        
    (executable, args.toArray)
  }

  def getScalaArgs(scalaHome: String): (String, Array[String]) = {
    val args = new ArrayBuffer[String]()

    val executable = getJavaHome + File.separator + "bin" + File.separator + "java" // NOI18N   
    // XXX Do I need java.exe on Windows?

    // Additional execution flags specified in the Scala startup script:
    args += "-Xverify:none" // NOI18N
    args += "-da" // NOI18N
            
    val extraArgs = System.getenv("SCALA_EXTRA_VM_ARGS") // NOI18N

    var javaMemory = "-Xmx512m" // NOI18N
    var javaStack = "-Xss1024k" // NOI18N
            
    if (extraArgs ne null) {
      if (extraArgs.indexOf("-Xmx") != -1) { // NOI18N
        javaMemory = null
      }
      if (extraArgs.indexOf("-Xss") != -1) { // NOI18N
        javaStack = null
      }
      val scalaArgs = Utilities.parseParameters(extraArgs)
      args ++= scalaArgs
    }
            
    if (javaMemory ne null) {
      args += javaMemory
    }
    if (javaStack ne null) {
      args += javaStack
    }
            
    val scalaHomeDir = try {
      new File(scalaHome).getCanonicalFile
    } catch  {
      case ex:IOException => Exceptions.printStackTrace(ex); null
    }

    val scalaLib = new File(scalaHomeDir, "lib") // NOI18N

    // BootClassPath
    args += "-Xbootclasspath/a:" + mkClassPath(Array(
        "scala-library.jar",
        "scala-reflect.jar",
        "scala-compiler.jar",
        "jline.jar"
      ), scalaLib)   
            
    // Classpath
    args += "-classpath" // NOI18N


//            argvList.add(computeScalaClassPath(
//                    descriptor eq null ? null : descriptor.getClassPath(), scalaLib));
            
    args += computeScalaClassPath(null, scalaLib)
            
    args += "-Dscala.home=" + scalaHomeDir // NOI18N
            
    /** 
     * @Note:
     * jline's UnitTerminal will hang in my Mac OS, when call "stty(...)", why? 
     * Also, from Scala-2.7.1, jline is used for scala shell, we should 
     * disable it here by add "-Djline.terminal=jline.UnsupportedTerminal"
     */
    //args += "-Djline.terminal=scala.tools.jline.UnsupportedTerminal" 
    args += "-Djline.WindowsTerminal.directConsole=false" //NOI18N
            
    // TODO - turn off verifier?

    // Main class
    args += SCALA_MAIN_CLASS // NOI18N

    // Application arguments follow
        
    (executable, args.toArray)
  }

  def getJavaHome: String = {
    System.getProperty("scala.java.home") match {  // NOI18N
      case null => System.getProperty("java.home") // NOI18N
      case x => x
    }
  }
  
  def getScalaHome: String = {
    System.getenv("SCALA_HOME") match { // NOI18N
      case null => 
        val d = new NotifyDescriptor.Message(
          "SCALA_HOME environment variable may not be set, or is invalid.\n" +
          "Please set SCALA_HOME first!", NotifyDescriptor.INFORMATION_MESSAGE)
        DialogDisplayer.getDefault().notify(d)
        null
      case scalaHome => System.setProperty("scala.home", scalaHome); scalaHome
    }
  }

  def getSbtHome: String = {
    //System.getProperty("user.home", "~") + File.separator + "myapps" + File.separator + "sbt"
    System.getProperty("netbeans.sbt.home") match {
      case null => null
      case x => x
    }
  }
  
  def getScala: File = {
    var scalaFo: FileObject = null
    val scalaHome = getScalaHome
    if (scalaHome ne null) {
      val scalaHomeDir = new File(getScalaHome)
      if (scalaHomeDir.exists && scalaHomeDir.isDirectory) {
        try {
          val scalaHomeFo = FileUtil.createData(scalaHomeDir)
          val bin = scalaHomeFo.getFileObject("bin")             //NOI18N
          if (Utilities.isWindows) {
            scalaFo = bin.getFileObject("scala", "exe")
            if (scalaFo eq null) {
              scalaFo = bin.getFileObject("scala", "bat")
            }
          } else {
            scalaFo = bin.getFileObject("scala", null)    //NOI18N
          }
        } catch  {
          case ex : IOException => Exceptions.printStackTrace(ex)
        }
      }
    }
    if (scalaFo ne null) {
      FileUtil.toFile(scalaFo)
    } else {
      val d = new NotifyDescriptor.Message(
        "Can not found ${SCALA_HOME}/bin/scala, the environment variable SCALA_HOME may be invalid.\n" +
        "Please set proper SCALA_HOME first!", NotifyDescriptor.INFORMATION_MESSAGE)
      DialogDisplayer.getDefault().notify(d)
      null
    }
  }
    
  def getSbtLaunchJar(sbtHome: String = null): File = {
    sbtHome match {
      case null | "" => getEmbeddedSbtLaunchJar
      case _ => 
        val jar = new File(sbtHome) match {
          case homeDir if (homeDir.exists && homeDir.isDirectory) =>
            try {
              val homeFo = FileUtil.createData(homeDir)
              val binDir = homeFo.getFileObject("bin")
              binDir.getFileObject("sbt-launch", "jar")
            } catch  {
              case ex: IOException => Exceptions.printStackTrace(ex); null
            }
          case _ => null
        }
        if (jar ne null) {
          FileUtil.toFile(jar)
        } else {
          val msg = new NotifyDescriptor.Message(
            "Can not found" + sbtHome + "/bin/sbt-launch.jar\n" +
            "Please set proper sbt home first!", NotifyDescriptor.INFORMATION_MESSAGE)
          DialogDisplayer.getDefault().notify(msg)
          null
        }
    }
  }
  
  private def getEmbeddedSbtLaunchJar: File = {
    val sbtDir = InstalledFileLocator.getDefault.locate("modules/ext/org.scala-sbt",  "org.netbeans.libs.sbt", false)
    if (sbtDir != null && sbtDir.exists && sbtDir.isDirectory) {
      sbtDir.listFiles find {jar =>
        val name = jar.getName
        (name == "sbt-launch" || name.startsWith("sbt-launch-")) && name.endsWith(".jar")
      } foreach {return _}
    }
    
    null
  }
  
  /**
   * Add settings in the environment appropriate for running Scala:
   * add the given directory into the path, and set up SCALA_HOME
   */
//    public @Override void setupProcessEnvironment(Map<String, String> env) {
//        super.setupProcessEnvironment(env);
//        env.put("JAVA_HOME", getJavaHome());
//        env.put("SCALA_HOME", getScalaHome());
//    }
    
  private def mkClassPath(jarNames: Array[String], dir: File) = {
    val dirPath = dir.getAbsolutePath 
    jarNames map (dirPath + File.separator + _) filter {fileName => 
      try {
        val file = new File(fileName)
        file.exists && file.canRead
      } catch {
        case ex: Throwable => false
      }
    } mkString File.pathSeparator
  }
  
  /** Package-private for unit test. */
  def computeScalaClassPath(extraCp: String, scalaLib: File): String = {
    val sb = new StringBuilder()
    val libs = scalaLib.listFiles

    libs filter (_.getName.endsWith("jar")) foreach {lib => 
      if (sb.length > 0) sb.append(File.pathSeparatorChar)
      sb.append(lib.getAbsolutePath)
    }

    // Add in user-specified jars passed via SCALA_EXTRA_CLASSPATH

    val p = new StringBuilder()
    if ((extraCp ne null) && File.pathSeparatorChar != ':') {
      // Ugly hack - getClassPath has mixed together path separator chars
      // (:) and filesystem separators, e.g. I might have C:\foo:D:\bar but
      // obviously only the path separator after "foo" should be changed to ;
      var pathOffset = 0
      extraCp foreach {c => 
        if (c == ':' && pathOffset != 1) {
          p += File.pathSeparatorChar
          pathOffset = 0           
        } else {
          pathOffset += 1
          p += c
        }
      }
      
    }

    if (p.isEmpty && (System.getenv("SCALA_EXTRA_CLASSPATH") ne null)) {
      p ++= System.getenv("SCALA_EXTRA_CLASSPATH") // NOI18N
    }

    if (!p.isEmpty) {
      if (sb.length > 0) {
        sb.append(File.pathSeparatorChar)
      }
      //if (File.pathSeparatorChar != ':' && extraCp.indexOf(File.pathSeparatorChar) == -1 &&
      //        extraCp.indexOf(':') != -1) {
      //    extraCp = extraCp.replace(':', File.pathSeparatorChar);
      //}
      sb.append(p)
    }
    if (Utilities.isWindows)  "\"" + sb.toString + "\""  else sb.toString // NOI18N
  }
  
  def getMainProjectWorkPath: File = {
    var pwd: File = null
    val mainProject = OpenProjects.getDefault.getMainProject
    if (mainProject != null) {
      var fo = mainProject.getProjectDirectory
      if (!fo.isFolder) {
        fo = fo.getParent
      }
      pwd = FileUtil.toFile(fo)
    }
    if (pwd == null) {
      val userHome = System.getProperty("user.home")
      pwd = new File(userHome, "project")
      if (pwd.exists) {
        pwd = if (pwd.isDirectory) new File(userHome) else pwd
      } else {
        pwd.mkdir
        pwd
      }
    }
    pwd
  }
  

     
}