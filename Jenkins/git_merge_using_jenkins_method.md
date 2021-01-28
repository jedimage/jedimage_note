**<< git merge using jenkins >>**

**Method1**: Merge before build
last part of Source code management "Additional Behaviours" -> Merge before build
Name of repository - such as origin, that contains the branch. If left blank, it’ll default to the name of the first repository configured.
Branch to merge to - Which branch within the named repository to merge to. such as master.

**Method2**: Accept gitlab merge request on success
To accept a merge request when build is completed from optional Post-build Actions
Post-build Actions -> Add post-build action -> Accept Gitlab merge request on success
No idea what this can do. Which branch want to merge to which branch ??????

For pipeline jobs two advanced configuration options can be provided
useMRDescription - Adds the merge request description into the merge commit, in a similar format as would be recieved by selecting 'Modify commit message' followed by 'include description in commit message' in GitLab UI
removeSourceBranch - Removes the source branch in GitLab when the merge request is accepted

**Method3**: Git Publisher (high-possibility)
The git publisher can push commits or tags from the workspace of a Freestyle project to the remote repository.
Post-build Action -> Add post-build action -> Git Publisher and fill in the information

Git Publisher
- Push Only If Build Succeeds: Only push changes from the workspace to the remote repository if the build succeeds. If the build status is unstable, failed, or canceled, the changes from the workspace will not be pushed.
- Merge Results: If pre-build merging is configured through one of the merge extensions, then enabling this checkbox will push the merge to the remote repository.
- Force Push: Git refuses to replace a remote commit with a different commit. This prevents accidental overwrite of new commits on the remote repository. However, there may be times when overwriting commits on the remote repository is acceptable and even desired. If the commits from the local workspace should overwrite commits on the remote repository, enable this option. It will request that the remote repository destroy history and replace it with history from the workspace.

Branch
- Branch to push: The name of the remote branch that will receive the latest commits from the agent workspace. This is usually the same branch that was used for the checkout such as `master`, `dev`, `uat`
- Target remote name: The shortname of the remote that will receive the latest commits from the agent workspace. Usually this is `origin`. It needs to be a shortname that is defined in the agent workspace, either through the initial checkout or through later configuration.

**Method4**: git merge using shell script (high-possibility)
Add key to use per repo and you groovy code like
```
sshagent(['git-credentials-id']) {
  sh "git push origin master"
}
```
to run git command to repo

- method1,2 ref: https://github.com/jenkinsci/gitlab-plugin
- method3 ref: https://plugins.jenkins.io/git/
- method4 ref: https://stackoverflow.com/questions/38769976/is-it-possible-to-git-merge-push-using-jenkins-pipeline