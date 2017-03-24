#!/usr/bin/env python
# coding=utf8
import os

os.chdir(os.path.dirname(os.path.realpath(__file__)))


def update_manifest_version(new_version):
    origin_version = r'"version":.*'
    new_version = r'"version": "%s",' % new_version
    cmd = "sed -i '' 's#%s#%s#' src/manifest.json" % (origin_version, new_version)
    os.system(cmd)

def reset_version(brower):
    with open('%s_version.txt' % brower) as f:
        version_num = f.read().strip()
    # 替换为当前浏览器的版本
    update_manifest_version(version_num)

if __name__ == '__main__':

    brower = 'chrome'

    reset_version(brower)

    dest_zip = os.path.expanduser('~/Desktop/%s_gooreplacer.zip') % brower
    if os.path.isfile(dest_zip):
        print('remove old zip %s' % dest_zip)
        os.remove(dest_zip)

    cmd = r'cd src;zip -x *.DS_Store -r %(dest_zip)s *' % {
        "dest_zip": dest_zip
    }
    os.system(cmd)

    brower = 'firefox'

    reset_version(brower)

    # read secret from ENV
    cmd = "cd src;web-ext sign --api-key=$AMO_JWT_ISSUER --api-secret=$AMO_JWT_SECRET"
    os.system(cmd)
    # 还原为初始化状态
    update_manifest_version('1.0')
