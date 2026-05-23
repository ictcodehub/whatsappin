# RTK (Rust Token Killer) - Token Optimization

RTK is installed at `C:\Users\HeyTAYO\.local\bin\rtk.exe` and available in PATH as `rtk`.

## Rules

When executing shell commands, ALWAYS prefer RTK-wrapped versions to save tokens and credits:

### Git Commands
- Instead of `git status` → use `rtk git status`
- Instead of `git diff` → use `rtk git diff`
- Instead of `git log` → use `rtk git log`

### File & Directory Operations
- Instead of `ls` or `dir` → use `rtk ls`
- Instead of `tree` → use `rtk tree`
- For reading files in shell → use `rtk read <file>`

### Testing
- Instead of `npm test` → use `rtk test npm test`
- Instead of `npx vitest --run` → use `rtk vitest --run`
- Instead of `npx jest` → use `rtk jest`
- Instead of `cargo test` → use `rtk cargo test`

### Build & Lint
- Instead of `npx tsc` → use `rtk tsc`
- Instead of `npx eslint .` → use `rtk lint`
- Instead of `npx prettier --check .` → use `rtk prettier --check .`
- Instead of `npm run build` (Next.js) → use `rtk next build`

### Package Management
- Instead of `npm install` → use `rtk npm install`
- Instead of `pnpm install` → use `rtk pnpm install`

### Error-Only Output
- When only errors matter → use `rtk err <command>`

### Dependencies Summary
- To check project deps → use `rtk deps`

### Docker & Kubernetes
- Instead of `docker ps` → use `rtk docker ps`
- Instead of `kubectl get pods` → use `rtk kubectl get pods`

## When NOT to use RTK
- Interactive commands (editors, REPLs)
- Commands where full unfiltered output is explicitly needed
- When debugging RTK itself
