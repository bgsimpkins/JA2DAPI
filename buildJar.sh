version=$1
platform=$2
buildDirs="files images sounds GameTest twodapi net org"

if [ "$version" = "" ] 
	then echo "Warning: No version specified!"
fi

if [ "$platform" = "windows" ] 
	#then natives="windows/jinput-dx8_64.dll jinput-dx8.dll jinput-raw_64.dll jinput-raw.dll lwjgl64.dll lwjgl.dll"
	then natives="windows/*"
elif [ "$platform" = "mac" ]
	#then natives="libjinput-osx.jnilib liblwjgl.jnilib"
	then natives="macosx/*"	
elif [ "$platform" = "linux" ]
	#then natives="libjinput-linux64.so libjinput-linux.so liblwjgl64.so liblwjgl.so"
	then natives="linux/*"
elif [ "$platform" = "freebsd" ]
	then natives="freebsd/*"
elif [ "$platform" = "solaris" ]
	then natives="solaris/*"
else echo "Warning: No supported platform specified! Please enter windows, mac, linux, freebsd or solaris"
	echo "Defaulting to Windows."
	natives="windows/*"
fi

cd dist
echo "Extracting classes..."
jar xf JA2DAPI.jar
jar xf ../lib/lwjgl.jar
jar xf ../lib/lwjgl_util.jar
jar xf ../lib/jinput.jar

echo "Building jar JA2DAPI$version.jar"
jar cf "JA2DAPI$version.jar" $buildDirs

echo "Adding native $platform libs..."
releaseDir="JA2DAPI$version"
if [ -d "$releaseDir" ]
	then rm -r $releaseDir
fi
mkdir $releaseDir
mkdir $releaseDir/lib
mkdir $releaseDir/lib/native
mv "JA2DAPI$version.jar" $releaseDir
cd ../lib/native
cp $natives ../../dist/$releaseDir/lib/native

echo "Cleaning up...."
cd ../../dist
rm -r $buildDirs META-INF


