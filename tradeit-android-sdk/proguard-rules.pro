-dontwarn okio.**
-dontwarn org.slf4j.**
-dontwarn ch.qos.logback.**
-dontwarn retrofit2.Platform$Java8
-dontwarn javax.annotation.**

-printmapping out.map

-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated, SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep public class it.trade.** {
      public protected *;
}

-keepclassmembernames class it.trade.** {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

-keepclasseswithmembernames,includedescriptorclasses class it.trade.** {
    native <methods>;
}

-keepclassmembers,allowoptimization enum it.trade.** {
    public static **[] values(); public static ** valueOf(java.lang.String);
}

-keepclassmembers class it.trade.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}