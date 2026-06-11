const { execSync } = require('child_process');

try {
    console.log('Starting Netlify build...');

    // 安装依赖
    console.log('Installing dependencies...');
    execSync('npm install', { stdio: 'inherit' });

    // 构建项目（不进行类型检查）
    console.log('Building project...');
    execSync('npx vite build', { stdio: 'inherit' });

    console.log('Build completed successfully!');
} catch (error) {
    console.error('Build failed:', error.message);
    process.exit(1);
}