/**
 * Header Component
 * 
 * Top navigation bar with logo, navigation links, and user menu.
 */

import { AppBar, Toolbar, Typography, Box, IconButton } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { useNavigate } from 'react-router-dom';

interface HeaderProps {
  onMenuClick?: () => void;
}

export default function Header({ onMenuClick }: HeaderProps) {
  const navigate = useNavigate();

  return (
    <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
      <Toolbar>
        <IconButton
          color="inherit"
          edge="start"
          onClick={onMenuClick}
          sx={{ mr: 2, display: { sm: 'none' } }}
        >
          <MenuIcon />
        </IconButton>
        
        <Typography
          variant="h6"
          component="div"
          sx={{ cursor: 'pointer', flexGrow: 0, mr: 4 }}
          onClick={() => navigate('/')}
        >
          🚀 AI Database Intelligence
        </Typography>

        <Box sx={{ flexGrow: 1 }} />

        {/* Future: User menu, notifications, etc. */}
        <Typography variant="body2" sx={{ display: { xs: 'none', sm: 'block' } }}>
          Convert Natural Language to SQL
        </Typography>
      </Toolbar>
    </AppBar>
  );
}

