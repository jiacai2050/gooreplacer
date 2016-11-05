#!/usr/bin/env python
# coding=utf8
import os

os.chdir(os.path.dirname(os.path.realpath(__file__)))

browers = ['chrome', 'firefox']
package_cmd_template = r'cd src;zip -x *.DS_Store -r %(dest_zip)s *'
zip_file = os.path.expanduser('~/Desktop/%s_gooreplacer.zip')
origin_version = r'"version":.*'


def update_manifest_version(new_version):
    new_version = r'"version": "%s",' % new_version
    cmd = "sed -i '' 's#%s#%s#' src/manifest.json" % (origin_version, new_version)
    print(cmd)
    os.system(cmd)

if __name__ == '__main__':
    for brower in browers:
        with open('%s_version.txt' % brower) as f:
            version_num = f.read().strip()

        # 替换为当前浏览器的版本
        update_manifest_version(version_num)

        dest_zip = zip_file % brower
        if os.path.isfile(dest_zip):
            print('remove old zip %s' % dest_zip)
            os.remove(dest_zip)

        cmd = package_cmd_template % {
            "dest_zip": dest_zip
        }
        print(cmd)
        os.system(cmd)

    # 还原为初始化状态
    update_manifest_version('1.0')
